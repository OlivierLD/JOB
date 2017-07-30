package io.samples;

import processing.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LedCounter {
  private LED leds[];

  // the Raspberry Pi has two build-in LEDs we can control
  // led0 (green) and led1 (red)
  public LedCounter() {
    this.setup();
  }

  private void setup() {
    String available[] = LED.list();
    System.out.println(String.format("Available: %s",
      Arrays.asList(available)
        .stream()
        .collect(Collectors.joining(", "))));

    // create an object for each LED and store it in an array
    leds = new LED[available.length];
    for (int i=0; i < available.length; i++) {
      leds[i] = new LED(available[i]);
    }
  }

  private int frameCount = 0;
  public void flip() {
    // make the LEDs count in binary
    for (int i=0; i < leds.length; i++) {
      if ((frameCount & (1 << i)) != 0) {

        leds[i].brightness(1.0f);
      } else {
        leds[i].brightness(0.0f);
      }
    }
    System.out.println(frameCount);
    frameCount++;
  }

  public void shutdown() {
    // cleanup
    for (int i=0; i < leds.length; i++) {
      leds[i].close();
    }
  }

  public static void main(String... args) {
    LedCounter ledCounter = new LedCounter();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      ledCounter.shutdown();
    }));

    for (int i=0; i<10; i++) {
      ledCounter.flip();
      try { Thread.sleep(1000L); } catch (InterruptedException ignore) {}
    }
    System.out.println("Bye");
  }
}
