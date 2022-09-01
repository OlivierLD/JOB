package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Input from a push button
 */
public class SimpleInput {

  private static int pin = 27; // GPIO 27. Physical pin #13. Override with -Dpin=12 (<- physical pin #32)
  private final static boolean VERBOSE = "true".equals(System.getProperty("verbose"));

  private static boolean buttonPressed = false;

  public SimpleInput() {
    this.setup();
  }

  private void setup() {
    System.out.printf("Initializing with pin #%d\n", SimpleInput.pin);
    GPIO.pinMode(SimpleInput.pin, GPIO.INPUT);
    System.out.printf("After setup, pin #%d is %s\n", SimpleInput.pin, GPIO.digitalRead(SimpleInput.pin) == GPIO.HIGH ? "HIGH:" : "LOW");
  }

  private void check() {
//    System.out.print(".");
    // sense the input pin
    if (GPIO.digitalRead(SimpleInput.pin) == GPIO.HIGH) {
      if (VERBOSE || !buttonPressed) {
        System.out.println("\nHigh");
      }
      buttonPressed = true;
    } else {
      if (VERBOSE || buttonPressed) {
        System.out.println("\nLow");
      }
      buttonPressed = false;
    }
  }

  private static boolean go = true; // Global, then no need for AtomicBoolean
  public static void main(String... args) {

    try {
      pin = Integer.parseInt(System.getProperty("pin", String.valueOf(pin)));
      System.out.printf("Will use pin#%d\n", pin);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.printf("Keeping default pin value %d\n", pin);
    }

    SimpleInput simpleInput = new SimpleInput();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> go = false, "Interrupter"));
    System.out.println("Ctrl-C to stop.");
    while (go) {
      simpleInput.check(); // Constant polling...
    }
    System.out.println("Bye");
  }
}
