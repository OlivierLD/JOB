package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Blink a LED on a GPIO pin.
 */
public class GPIOLed {

  private final int pin = 18; // Physical pin #12

  public GPIOLed() {
    this.setup();
  }

  private void setup() {
    GPIO.pinMode(this.pin, GPIO.OUTPUT);
  }

  private void check() {
    // sense the input pin
    if (GPIO.digitalRead(this.pin) == GPIO.HIGH) {
      System.out.println("High");
    } else {
      System.out.println("Low");
    }
  }

  private void flip() {
    if (GPIO.digitalRead(this.pin) == GPIO.HIGH) {
      GPIO.digitalWrite(this.pin, false);
    } else {
      GPIO.digitalWrite(this.pin, true);
    }
  }

  private static boolean go = true; // Global, then no need for AtomicBoolean

  public static void main(String... args) {
    GPIOLed simpleInput = new GPIOLed();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> go = false, "Interrupter"));
    while (go) {
      simpleInput.check(); // Constant polling...
    }
    System.out.println("Bye");
  }
}
