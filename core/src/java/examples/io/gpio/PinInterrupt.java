package examples.io.gpio;

import jhard.io.GPIO;

/**
 * Input from a push button
 */
public class PinInterrupt {
// GPIO numbers refer to different physical pins on various boards
// On the Raspberry Pi GPIO 4 is physical pin 7 on the header
// see setup.png in the sketch folder for wiring details
  private int pin = 27;

  public PinInterrupt() {
    this.setup();
  }

  private void setup() {
    GPIO.pinMode(pin, GPIO.INPUT);
    GPIO.attachInterrupt(pin, this, "buttonListener", GPIO.CHANGE);

	  // TODO In the line above, replace the parent and methodName with a Consumer<Integer>
//  GPIO.attachInterrupt(pin, this::buttonListener, GPIO.CHANGE);
  }

  private void buttonListener(int i) {
    System.out.println(String.format("Interrupt! Prm: %d", i));
  }

  public void check() {
    // sense the input pin
    if (GPIO.digitalRead(pin) == GPIO.HIGH) {
      System.out.println("High");
    } else {
      System.out.println("Low");
    }
  }

  public static void main(String... args) {

  	new PinInterrupt();

    final Thread me = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      synchronized (me) {
      	me.notify();
	    }
    }));

    synchronized (me) {
	    try {
		    me.wait();
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }
    }
    System.out.println("Bye");
  }
}
