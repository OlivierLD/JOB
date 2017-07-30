package io.samples;

import processing.io.*;

/**
 * Input from a push button
 */
public class SimpleInput {
// GPIO numbers refer to different phyiscal pins on various boards
// On the Raspberry Pi GPIO 4 is physical pin 7 on the header
// see setup.png in the sketch folder for wiring details
  private int pin = 27;

  public SimpleInput() {
    this.setup();
  }

  private void setup() {
    GPIO.pinMode(pin, GPIO.INPUT);
  }

  public void check() {
    // sense the input pin
    if (GPIO.digitalRead(pin) == GPIO.HIGH) {
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
      simpleInput.check();
    }
    System.out.println("Bye");
  }
}
