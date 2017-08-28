package examples.io.gpio;

import jhard.io.GPIO;

/**
 * Input from a push button
 */
public class PinInterrupt {
// GPIO numbers refer to different physical pins on various boards
// On the Raspberry Pi, GPIO 4 is physical pin 7 on the header
// see setup.png in the sketch folder for wiring details.
  private int pin = 27; // Physical pin #13

  public PinInterrupt() {
    this.setup();
  }

  private void setup() {
    GPIO.pinMode(this.pin, GPIO.INPUT);
//  GPIO.attachInterrupt(pin, this, "buttonListener", GPIO.CHANGE);
	  // In the line above, replace the parent and methodName with a Consumer<Integer>, and make it private.
	  GPIO.attachInterrupt(this.pin, this::buttonPressed, GPIO.RISING);
  }

//  public void buttonListener(int i) {
//    System.out.println(String.format("Interrupt! pin: %d", i));
//  }

	private void buttonPressed(int i) {
    System.out.println(String.format("Button pressed pin: %d", i));
  }

  public static void main(String... args) {

  	new PinInterrupt();

    final Thread me = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      synchronized (me) {
      	me.notify();
	      System.out.println(" <- Ooops!");
	    }
    }));

	  System.out.println("Ready.");
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
