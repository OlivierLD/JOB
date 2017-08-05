package jhard.io;

/**
 *
 */
public class SoftwareServo {

  public static final int DEFAULT_MIN_PULSE = 544;
  public static final int DEFAULT_MAX_PULSE = 2_400;

  protected int pin = -1;           // gpio number (-1 .. not attached)
  protected long handle = -1;       // native thread id (<0 .. not started)
  protected int period = 20_000;    // 20 ms (50 Hz)
  protected int minPulse = 0;       // minimum pulse width in microseconds
  protected int maxPulse = 0;       // maximum pulse width in microseconds
  protected int pulse = 0;          // current pulse in microseconds

  /**
   *  Opens a servo motor
   *  @param parent typically use "this"
   */
  public SoftwareServo(Object parent) {
    JHardNativeInterface.loadLibrary();
  }


  /**
   *  Closes a servo motor
   */
  public void close() {
    detach();
  }


  protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    }
  }

  /**
   *  Attaches a servo motor to a GPIO pin
   *  @param pin GPIO pin
   */
  public void attach(int pin) {
    detach();
    this.pin = pin;
    this.minPulse = DEFAULT_MIN_PULSE;
    this.maxPulse = DEFAULT_MAX_PULSE;
  }

  /**
   *  Attaches a servo motor to a GPIO pin using custom pulse widths
   *  @param minPulse minimum pulse width in microseconds (default: 544, same as on Arduino)
   *  @param maxPulse maximum pulse width in microseconds (default: 2400, same as on Arduino)
   */
  public void attach(int pin, int minPulse, int maxPulse) {
    detach();
    this.pin = pin;
    this.minPulse = minPulse;
    this.maxPulse = maxPulse;
  }

  /**
   *  Moves a servo motor to a given orientation
   *  @param angle angle in degrees (controls speed and direction on continuous-rotation servos)
   */
  public void write(float angle) {
    if (attached() == false) {
      System.err.println("You need to call attach(pin) before write(angle).");
      throw new RuntimeException("Servo is not attached");
    }

    if (angle < 0 || 180 < angle) {
      System.err.println("Only degree values between 0 and 180 can be used.");
      throw new IllegalArgumentException("Illegal value");
    }
    pulse = (int)(minPulse + (angle/180.0) * (maxPulse-minPulse));

    if (handle < 0) {
      // start a new thread
      GPIO.pinMode(pin, GPIO.OUTPUT);
      if (JHardNativeInterface.isSimulated()) {
        return;
      }
      handle = JHardNativeInterface.servoStartThread(pin, pulse, period);
      if (handle < 0) {
        throw new RuntimeException(JHardNativeInterface.getError((int)handle));
      }
    } else {
      // thread already running
      int ret = JHardNativeInterface.servoUpdateThread(handle, pulse, period);
      if (ret < 0) {
        throw new RuntimeException(JHardNativeInterface.getError(ret));
      }
    }
  }

  /**
   *  Returns whether a servo motor is attached to a pin
   *  @return true if attached, false is not
   */
  public boolean attached() {
    return (pin != -1);
  }

  /**
   *  Detach a servo motor from a GPIO pin
   */
  public void detach() {
    if (0 <= handle) {
      // stop thread
      int ret = JHardNativeInterface.servoStopThread(handle);
      GPIO.pinMode(pin, GPIO.INPUT);
      handle = -1;
      pin = -1;
      if (ret < 0) {
        throw new RuntimeException(JHardNativeInterface.getError(ret));
      }
    }
  }
}
