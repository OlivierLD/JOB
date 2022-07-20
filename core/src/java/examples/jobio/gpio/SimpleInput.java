package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Input from a push button
 */
public class SimpleInput {

  private static int pin = 27; // Physical pin #13. Override with -Dpin=12 (<- physical 32)

  public SimpleInput() {
    this.setup();
  }

  private void setup() {
    GPIO.pinMode(this.pin, GPIO.INPUT);
  }

  private void check() {
    // sense the input pin
    if (GPIO.digitalRead(this.pin) == GPIO.HIGH) {
      System.out.println("High");
    } else {
      System.out.println("Low");
    }
  }

  private static boolean go = true; // Global, then no need for AtomicBoolean
  public static void main(String... args) {

    try {
      pin = Integer.parseInt(System.getProperty("pin", String.valueOf(pin)));
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.printf("Keeping default pin value %d\n", pin);
    }

    SimpleInput simpleInput = new SimpleInput();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> go = false, "Interrupter"));
    while (go) {
      simpleInput.check(); // Constant polling...
    }
    System.out.println("Bye");
  }
}
