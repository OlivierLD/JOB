package examples.jobio.led;

import job.io.LED;

import java.util.Arrays;

public class LedCounter {
  private LED[] leds;

  // the Raspberry Pi has two build-in LEDs we can control
  // led0 (green) and led1 (red)
  public LedCounter() {
    this.setup();
  }

  private void setup() {
    String[] available = LED.list();
    System.out.printf("Available: %s\n", String.join(", ", available));

    // create an object for each LED and store it in an array
    leds = new LED[available.length];
    for (int i=0; i < available.length; i++) {
      leds[i] = new LED(available[i]);
    }
  }

  private int frameCount = 0;
  public void flip() {
    frameCount++;
    for (int i=0; i < leds.length; i++) {
      if ((frameCount % (i + 1)) == 0) {
        leds[i].brightness(1.0f);
      } else {
        leds[i].brightness(0.0f);
      }
    }
    System.out.println(frameCount);
  }

  public void shutdown() {
    // cleanup
    Arrays.stream(leds).forEach(LED::close);
  }

  public static void main(String... args) {
    LedCounter ledCounter = new LedCounter();

    Runtime.getRuntime().addShutdownHook(new Thread(ledCounter::shutdown, "Interrupter"));

    for (int i=0; i<10; i++) {
      ledCounter.flip();
      try { Thread.sleep(1000L); } catch (InterruptedException ignore) {}
    }
    System.out.println("Bye");
  }
}
