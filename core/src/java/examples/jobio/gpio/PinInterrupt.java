package examples.jobio.gpio;

import job.io.GPIO;

/**
 * Input from a push button
 */
public class PinInterrupt {

    private static int PIN = 27; // Physical pin #13, override with -Dpin=XX

    public PinInterrupt() {
        this.setup();
    }

    private void setup() {
        GPIO.pinMode(PinInterrupt.PIN, GPIO.INPUT);
//  GPIO.attachInterrupt(pin, this, "buttonListener", GPIO.CHANGE); // Deprecated
        // In the line above, replace the parent and methodName with a Consumer<Integer>, and make it private.
        GPIO.attachInterrupt(PinInterrupt.PIN, this::buttonChange, GPIO.CHANGE);

//        GPIO.attachInterrupt(PinInterrupt.PIN, this::buttonPressed, GPIO.RISING);
//        GPIO.releaseInterrupt(PinInterrupt.PIN); // Mhh ?
//        GPIO.attachInterrupt(PinInterrupt.PIN, this::buttonReleased, GPIO.FALLING);
    }

//  public void buttonListener(int i) {
//    System.out.println(String.format("Interrupt! pin: %d", i));
//  }

    private void buttonChange(int i) {

        if (GPIO.digitalRead(i) == GPIO.HIGH) {
            System.out.printf("Button Changed pin: %d, -> HIGH\n", i);
        }  else {
            System.out.printf("Button Changed pin: %d, -> LOW\n", i);
        }
    }

    private void buttonPressed(int i) {
        System.out.printf("Button pressed pin: %d\n", i);
    }

    private void buttonReleased(int i) {
        System.out.printf("Button released pin: %d\n", i);
    }

    public static void main(String... args) {

        try {
            PIN = Integer.parseInt(System.getProperty("pin", String.valueOf(PIN)));
            System.out.printf("Will use pin#%d\n", PIN);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.printf("Keeping default pin value %d\n", PIN);
        }

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
