package job.io;

import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 *  GPIO interface
 */
public class GPIO {

  // those constants are generally the same as in Arduino.h
  public static final int INPUT = 0;
  public static final int OUTPUT = 1;
  public static final int INPUT_PULLUP = 2;
  public static final int INPUT_PULLDOWN = 3;

  public static final int LOW = 0;
  public static final int HIGH = 1;

  public static final int NONE = 0;
  /**
   *  trigger when level changes
   */
  public static final int CHANGE = 1;
  /**
   *  trigger when level changes from high to low
   */
  public static final int FALLING = 2;
  /**
   *  trigger when level changes from low to high
   */
  public static final int RISING = 3;

  private final static int ENOENT =  -2;
	private final static int EINVAL = -22;
	private final static int EBUSY  = -16;

  protected static Map<Integer, Thread> irqThreads = new HashMap<>();
  protected static boolean serveInterrupts = true;
  protected static BitSet values = new BitSet();


  static {
    JOBNativeInterface.loadLibrary();
  }

  public boolean isSimulated() {
    return JOBNativeInterface.isSimulated();
  }

  public static void analogWrite(int pin, int value) {
    // currently this can't be done in a non-platform-specific way
    // the best way forward would be implementing a generic, "soft"
    // PWM in the kernel that uses high resolution timers, similiar
    // to the patch Bill Gatliff posted, which unfortunately didn't
    // get picked up, see
    // https://dev.openwrt.org/browser/trunk/target/linux/generic/files/drivers/pwm/gpio-pwm.c?rev=35328

    // additionally, there currently doesn't seem to be a way to link
    // a PWM channel back to the GPIO pin it is associated with

    // alternatively, this could be implemented in user-space to some
    // degree
    // see http://stackoverflow.com/a/13371570/3030124
    // see http://raspberrypi.stackexchange.com/a/304
    throw new RuntimeException("Not yet implemented");
  }


  /**
   *  Calls a function when the value of an input pin changes
   *  @param pin GPIO pin
   *  @param parent typically use "this"
   *  @param method name of sketch method to call
   *  @param mode when to call: GPIO.CHANGE, GPIO.FALLING or GPIO.RISING
   *  @see #noInterrupts
   *  @see #interrupts
   *  @see #releaseInterrupt
   */
  @Deprecated
  public static void attachInterrupt(int pin, Object parent, String method, int mode) {
    if (irqThreads.containsKey(pin)) {
      throw new RuntimeException("You must call releaseInterrupt before attaching another interrupt on the same pin");
    }

    enableInterrupt(pin, mode);

    final int irqPin = pin;
    final Object irqObject = parent;
    final Method irqMethod;
    try {
      irqMethod = parent.getClass().getMethod(method, int.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Method " + method + " does not exist in parent Object");
    }

    // it might be worth checking how Java threads compare to pthreads in terms
    // of latency
    Thread t = new Thread(() -> {
      boolean gotInterrupt = false;
      try {
        do {
          try {
            if (waitForInterrupt(irqPin, 100)) {
              gotInterrupt = true;
            }
            if (gotInterrupt && serveInterrupts) {
              irqMethod.invoke(irqObject, irqPin);
              gotInterrupt = false;
            }
            // if we received an interrupt while interrupts were disabled
            // we still deliver it the next time interrupts get enabled
            // not sure if everyone agrees with this logic though
          } catch (RuntimeException e) {
            // make sure we're not busy spinning on error
            Thread.sleep(100);
          }
        } while (!Thread.currentThread().isInterrupted());
      } catch (Exception e) {
        // terminate the thread on any unexpected exception that might occur
        System.err.println("Terminating interrupt handling for pin " + irqPin + " after catching: " + e.getMessage());
      }
    }, "GPIO" + pin + " IRQ");

    t.setPriority(Thread.MAX_PRIORITY);
    t.start();

    irqThreads.put(pin, t);
  }

	/**
	 *
	 * @param pin GPIO pin
	 * @param callback the event Consumer. Takes the pin # as prm.
	 * @param mode one of {@link #CHANGE}, {@link #FALLING}, {@link #RISING}.
	 *
	 */
  public static void attachInterrupt(int pin, Consumer<Integer> callback, int mode) {
	  if (irqThreads.containsKey(pin)) {
		  throw new RuntimeException("You must call releaseInterrupt before attaching another interrupt on the same pin");
	  }

	  enableInterrupt(pin, mode);

	  final int irqPin = pin;
	  // it might be worth checking how Java threads compare to pthreads in terms
	  // of latency
	  Thread t = new Thread(() -> {
          boolean gotInterrupt = false;
          try {
              do {
                  try {
                      if (waitForInterrupt(irqPin, 100)) {
                          gotInterrupt = true;
                      }
                      if (gotInterrupt && serveInterrupts) {
                          callback.accept(irqPin);
                          gotInterrupt = false;
                      }
                      // if we received an interrupt while interrupts were disabled
                      // we still deliver it the next time interrupts get enabled
                      // not sure if everyone agrees with this logic though
                  } catch (RuntimeException e) {
                      // make sure we're not busy spinning on error
                      Thread.sleep(100);
                  }
              } while (!Thread.currentThread().isInterrupted());
          } catch (Exception e) {
              // terminate the thread on any unexpected exception that might occur
              System.err.println("Terminating interrupt handling for pin " + irqPin + " after catching: " + e.getMessage());
          }
      }, "GPIO" + pin + " IRQ");

	  t.setPriority(Thread.MAX_PRIORITY);
	  t.start();

	  irqThreads.put(pin, t);
  }

    /**
		 *  Checks if the GPIO pin number can be valid
		 *
		 *  Board-specific classes, such as RPI, assign -1 to pins that carry power,
		 *  ground and the like.
		 *  @param pin GPIO pin
		 */
  protected static void checkValidPin(int pin) {
    if (pin < 0) {
      throw new RuntimeException("Operation not supported on this pin");
    }
  }


  /**
   *  Returns the value of an input pin
   *  @param pin GPIO pin
   *  @return GPIO.HIGH (1) or GPIO.LOW (0)
   *  @see #pinMode
   *  @see #digitalWrite
   */
  public static int digitalRead(int pin) {
    checkValidPin(pin);

    if (JOBNativeInterface.isSimulated()) {
      return LOW;
    }

    String fName = String.format("/sys/class/gpio/gpio%d/value", pin);
    byte in[] = new byte[2];
    int ret = JOBNativeInterface.readFile(fName, in);
    if (ret < 0) {
      throw new RuntimeException(JOBNativeInterface.getError(ret));
    } else if (1 <= ret && in[0] == '0') {
      return LOW;
    } else if (1 <= ret && in[0] == '1') {
      return HIGH;
    } else {
      System.err.print("Read " + ret + " bytes");
      if (0 < ret) {
        System.err.format(", first byte is 0x%02x" + in[0]);
      }
      System.err.println();
      throw new RuntimeException("Unexpected value");
    }
  }


  /**
   *  Sets an output pin to be either high or low
   *  @param pin GPIO pin
   *  @param value GPIO.HIGH (1) or GPIO.LOW (0)
   *  @see #pinMode
   *  @see #digitalRead
   */
  public static void digitalWrite(int pin, int value) {
    checkValidPin(pin);

    String out;
    if (value == LOW) {
      // values are also stored in a bitmap to make it possible to set a
      // default level per pin before enabling the output
      values.clear(pin);
      out = "0";
    } else if (value == HIGH) {
      values.set(pin);
      out = "1";
    } else {
      System.err.println("Only GPIO.LOW and GPIO.HIGH, 0 and 1, or true and false, can be used.");
      throw new IllegalArgumentException("Illegal value");
    }

    if (JOBNativeInterface.isSimulated()) {
      return;
    }

    String fName = String.format("/sys/class/gpio/gpio%d/value", pin);
    int ret = JOBNativeInterface.writeFile(fName, out);
    if (ret < 0) {
      if (ret != ENOENT) {    // Pin might not yet be exported
        throw new RuntimeException(JOBNativeInterface.getError(ret));
      }
    }
  }

  /**
   *  @param value true or false
   */
  public static void digitalWrite(int pin, boolean value) {
    digitalWrite(pin, value ? HIGH : LOW);
  }

  /**
   *  Disables an interrupt for an input pin
   *  @param pin GPIO pin
   *  @see #enableInterrupt
   *  @see #waitForInterrupt
   */
  protected static void disableInterrupt(int pin) {
    enableInterrupt(pin, NONE);
  }

  /**
   *  Enables an interrupt for an input pin
   *  @param pin GPIO pin
   *  @param mode what to wait for: GPIO.CHANGE, GPIO.FALLING or GPIO.RISING
   *  @see #waitForInterrupt
   *  @see #disableInterrupt
   */
  protected static void enableInterrupt(int pin, int mode) {
    checkValidPin(pin);

    String out;
    if (mode == NONE) {
      out = "none";
    } else if (mode == CHANGE) {
      out = "both";
    } else if (mode == FALLING) {
      out = "falling";
    } else if (mode == RISING) {
      out = "rising";
    } else {
      throw new IllegalArgumentException("Unknown mode");
    }

    if (JOBNativeInterface.isSimulated()) {
      return;
    }

    String fName = String.format("/sys/class/gpio/gpio%d/edge", pin);
    int ret = JOBNativeInterface.writeFile(fName, out);
    if (ret < 0) {
      if (ret == ENOENT) {
        System.err.println("Make sure your called pinMode on the input pin");
      }
      throw new RuntimeException(JOBNativeInterface.getError(ret));
    }
  }

  /**
   *  Allows interrupts to happen
   *  @see #attachInterrupt
   *  @see #noInterrupts
   *  @see #releaseInterrupt
   */
  public static void interrupts() {
    serveInterrupts = true;
  }

  /**
   *  Prevents interrupts from happpening
   *  @see #attachInterrupt
   *  @see #interrupts
   *  @see #releaseInterrupt
   */
  public static void noInterrupts() {
    serveInterrupts = false;
  }

  /**
   *  Configures a pin to act either as input or output
   *  @param pin GPIO pin
   *  @param mode GPIO.INPUT or GPIO.OUTPUT
   *  @see #digitalRead
   *  @see #digitalWrite
   *  @see #releasePin
   */
  public static void pinMode(int pin, int mode) {
    checkValidPin(pin);

    if (JOBNativeInterface.isSimulated()) {
      return;
    }

    // export pin through sysfs
    String fName = "/sys/class/gpio/export";
    int ret = JOBNativeInterface.writeFile(fName, Integer.toString(pin));
    if (ret < 0) {
      if (ret == ENOENT) {
        System.err.println("Make sure your kernel is compiled with GPIO_SYSFS enabled");
      }
      if (ret == EINVAL) {
        System.err.println("GPIO pin " + pin + " does not seem to be available on your platform");
      }
      if (ret != EBUSY) {   // returned when the pin is already exported
        throw new RuntimeException(fName + ": " + JOBNativeInterface.getError(ret));
      }
    }
    // set direction and default level for outputs
    fName = String.format("/sys/class/gpio/gpio%d/direction", pin);
    String out;
    if (mode == INPUT) {
      out = "in";
    } else if (mode == OUTPUT) {
      if (values.get(pin)) {
        out = "high";
      } else {
        out = "low";
      }
    } else if (mode == INPUT_PULLUP || mode == INPUT_PULLDOWN) {
      // currently this can't be done in a non-platform-specific way, see
      // http://lists.infradead.org/pipermail/linux-rpi-kernel/2015-August/002146.html
      throw new RuntimeException("Not yet implemented");
    } else {
      throw new IllegalArgumentException("Unknown mode");
    }
    // we need to give udev some time to change the file permissions behind our back
    // retry for 500ms when writing to the file fails with -EPERM
    long start = System.currentTimeMillis();
    do {
      ret = JOBNativeInterface.writeFile(fName, out);
    } while (ret == -1 && (System.currentTimeMillis() - start) < 500);

    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JOBNativeInterface.getError(ret));
    }
  }

  /**
   *  Stops listening for interrupts on an input pin
   *  @param pin GPIO pin
   *  @see #attachInterrupt
   *  @see #noInterrupts
   *  @see #interrupts
   */
  public static void releaseInterrupt(int pin) {
    Thread t = irqThreads.get(pin);
    if (t == null) {
      return;
    }

    t.interrupt();
    try {
      t.join();
    } catch (InterruptedException e) {
      System.err.println("Error joining thread in releaseInterrupt: " + e.getMessage());
    }
    t = null;
    irqThreads.remove(pin);

    disableInterrupt(pin);
  }

  /**
   *  Gives ownership of a pin back to the operating system
   *  @param pin GPIO pin
   *  @see #pinMode
   */
  public static void releasePin(int pin) {
    checkValidPin(pin);

    if (JOBNativeInterface.isSimulated()) {
      return;
    }

    String fName = "/sys/class/gpio/unexport";
    int ret = JOBNativeInterface.writeFile(fName, Integer.toString(pin));
    if (ret < 0) {
      if (ret == ENOENT) {
        System.err.println("Make sure your kernel is compiled with GPIO_SYSFS enabled");
      }
      // EINVAL is returned when trying to unexport pins that weren't exported to begin with, ignore this case
      if (ret != EINVAL) {
        throw new RuntimeException(JOBNativeInterface.getError(ret));
      }
    }
  }

  /**
   *  Waits for the value of an input pin to change
   *  @param pin GPIO pin
   *  @param mode what to wait for: GPIO.CHANGE, GPIO.FALLING or GPIO.RISING
   */
  public static void waitFor(int pin, int mode) {
    waitFor(pin, mode, -1);
  }


  /**
   *  Waits for the value of an input pin to change
   *
   *  This function will throw a RuntimeException in case of a timeout.
   *  @param timeout don't wait more than timeout milliseconds
   */
  public static void waitFor(int pin, int mode, int timeout) {
    enableInterrupt(pin, mode);
    if (!waitForInterrupt(pin, timeout)) {
      throw new RuntimeException("Timeout occurred");
    }
  }

  public static boolean waitForInterrupt(int pin, int mode, int timeout) {
    throw new RuntimeException("The waitForInterrupt function has been renamed to waitFor. Please update your sketch accordingly.");
  }

  /**
   *  Waits for the value of an input pin to change
   *
   *  Make sure to setup the interrupt with enableInterrupt() before calling
   *  this function. A timeout value of -1 waits indefinitely.
   *  @param pin GPIO pin
   *  @param timeout don't wait more than timeout milliseconds
   *  @return true if the interrupt occured, false if the timeout occured
   *  @see #enableInterrupt
   *  @see #disableInterrupt
   */
  protected static boolean waitForInterrupt(int pin, int timeout) {
    checkValidPin(pin);

    if (JOBNativeInterface.isSimulated()) {
      // pretend the interrupt happens after 200ms
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {}
      return true;
    }

    String fName = String.format("/sys/class/gpio/gpio%d/value", pin);
    int ret = JOBNativeInterface.pollDevice(fName, timeout);
    if (ret < 0) {
      if (ret == ENOENT) {    // ENOENT
        System.err.println("Make sure your called pinMode on the input pin");
      }
      throw new RuntimeException(JOBNativeInterface.getError(ret));
    } else if (ret == 0) {
      // timeout
      return false;
    } else {
      // interrupt
      return true;
    }
  }
}
