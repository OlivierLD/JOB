package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Input from a push button
 */
public class PinInterrupt {

    private static final int PIN = 27; // Physical pin #13

    public PinInterrupt() {
        this.setup();
    }

    private void setup() {
        GPIO.pinMode(PinInterrupt.PIN, GPIO.INPUT);
//  GPIO.attachInterrupt(pin, this, "buttonListener", GPIO.CHANGE); // Deprecated
        // In the line above, replace the parent and methodName with a Consumer<Integer>, and make it private.
        GPIO.attachInterrupt(PinInterrupt.PIN, this::buttonPressed, GPIO.RISING);
    }

//  public void buttonListener(int i) {
//    System.out.println(String.format("Interrupt! pin: %d", i));
//  }

    private void buttonPressed(int i) {
        System.out.printf("Button pressed pin: %d\n", i);
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
