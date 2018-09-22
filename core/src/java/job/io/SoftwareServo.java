package job.io;

/**
 *
 */
public class SoftwareServo {

	/**
	 * <b>In theory</b>, PWM servos support those values:
	 *<pre>
	 * Servo Pulse | Standard |   Continuous
	 * ------------+----------+-------------------
	 *       1.5ms |   0 deg  |     Stop
	 *       2.0ms |  90 deg  | FullSpeed forward
	 *       1.0ms | -90 deg  | FullSpeed backward
	 * ------------+----------+-------------------
	 *</pre>
	 * <b><i>BUT</i></b> this may vary a lot.<br/>
	 * Servos like <a href="https://www.adafruit.com/product/169">https://www.adafruit.com/product/169</a> or <a href="https://www.adafruit.com/product/155">https://www.adafruit.com/product/155</a>
	 * have min and max values like 0.5ms 2.5ms, which is quite different from the "theorical" values. Servos are analog devices...
	 * <br/>
	 * <pre>
	 * pulse = (int)(minPulse + (angle/180.0) * (maxPulse - minPulse));
	 * -90 : 544 + (  0 / 180) * (2400 - 544) =   544 => ~0.5ms
	 *   0 : 544 + ( 90 / 180) * (2400 - 544) = 1,472 => ~1.5ms
	 * +90 : 544 + (180 / 180) * (2400 - 544) = 2,400 => ~2.5ms
	 * </pre>
	 * The values below, [544..2400], sound suitable for the servos mentioned above.
	 */
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

  public boolean isSimulated() {
    return JHardNativeInterface.isSimulated();
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
    if (!this.attached()) {
      System.err.println("You need to call attach(pin) before write(angle).");
      throw new RuntimeException("Servo is not attached");
    }

    if (angle < 0 || angle > 180) {
      System.err.println("Only degree values between 0 and 180 can be used.");
      throw new IllegalArgumentException("Illegal value");
    }
    this.pulse = (int)(this.minPulse + (angle/180.0) * (this.maxPulse - this.minPulse));

    if (this.handle < 0) {
      // start a new thread
      GPIO.pinMode(this.pin, GPIO.OUTPUT);
      if (JHardNativeInterface.isSimulated()) {
        return;
      }
      this.handle = JHardNativeInterface.servoStartThread(this.pin, this.pulse, this.period);
      if (this.handle < 0) {
        throw new RuntimeException(JHardNativeInterface.getError((int)this.handle));
      }
    } else {
      // thread already running
      int ret = JHardNativeInterface.servoUpdateThread(this.handle, this.pulse, this.period);
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
    return (this.pin != -1);
  }

  /**
   *  Detach a servo motor from a GPIO pin
   */
  public void detach() {
    if (this.handle > 0) {
      // stop thread
      int ret = JHardNativeInterface.servoStopThread(this.handle);
      GPIO.pinMode(this.pin, GPIO.INPUT);
      this.handle = -1;
      this.pin = -1;
      if (ret < 0) {
        throw new RuntimeException(JHardNativeInterface.getError(ret));
      }
    }
  }
}
