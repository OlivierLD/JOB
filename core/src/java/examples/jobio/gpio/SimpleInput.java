package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Input from a push button
 */
public class SimpleInput {

  private int pin = 27; // Physical pin #13

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

  private static boolean go = true;
  public static void main(String... args) {
    SimpleInput simpleInput = new SimpleInput();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      go = false;
    }));
    while (go) {
      simpleInput.check(); // Constant polling...
    }
    System.out.println("Bye");
  }
}
