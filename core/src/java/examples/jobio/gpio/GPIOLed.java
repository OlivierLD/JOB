package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Blink a LED on a GPIO pin.
 */
public class GPIOLed {

  private final int pin = 18; // Physical pin #12
  private boolean ledIsOn = false;

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
    if (this.ledIsOn) {
      GPIO.digitalWrite(this.pin, false);
      this.ledIsOn = false;
    } else {
      GPIO.digitalWrite(this.pin, true);
      this.ledIsOn = true;
    }
  }

  private static boolean go = true; // Global, then no need for AtomicBoolean

  public static void main(String... args) {
    GPIOLed simpleInput = new GPIOLed();

    final Thread currentThread = Thread.currentThread();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      go = false;
      synchronized (currentThread) {
        try {
          currentThread.join();
          System.out.println("... User interrupted.");
        } catch (InterruptedException ie) {
          ie.printStackTrace();
        }
      }
    }, "Interrupter"));

    while (go) { // Flip LED every second.
      simpleInput.flip();
      try {
        Thread.sleep(1_000L);
      } catch (InterruptedException ie) {
        go = false;
      }
    }
    GPIO.digitalWrite(simpleInput.pin, false); // Turn it off
    System.out.println("Bye");
  }
}
