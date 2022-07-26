package examples.jobio.spi;

import job.io.SPI;
import job.devices.MCP3008;

/*
 * Wiring of the MCP3008-SPI:
 * +---------++---------------------------------------------+
 * | MCP3008 || Raspberry PI                                |
 * +---------++------+------------+---------+---------------+
 * |         || Pin# | Name       | GPIO    | wiringPI/PI4J |
 * +---------++------+------------+---------+---------------+
 * | CLK     ||  #23 | SPI0_CLK   | GPIO_11 |  14           |
 * | Din     ||  #21 | SPI0_MISO  | GPIO_9  |  13           |
 * | Dout    ||  #19 | SPI0_MOSI  | GPIO_10 |  12           |
 * | CS      ||  #24 | SPI0_CE0_N | GPIO_8  |  10           |
 * +---------++------+------------+---------+---------------+
 */

public class MCP3008Sample {

  private static boolean go = true;
  private final static int channel = 0;

  // Main for tests
  public static void main(String... args) {
    String[] available = SPI.list();
    System.out.printf("Available: %s\n", String.join(", ", available));

    MCP3008 adc = new MCP3008(SPI.list()[0]);

    final Thread currentThread = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      go = false;
      synchronized (currentThread) {
//                currentThread.notify(); // No thread is waiting...
        try {
          currentThread.join();
          System.out.println("\n... Joining");
        } catch (InterruptedException ie) {
          ie.printStackTrace();
        }
      }
    }, "Interrupter"));
    while (go) {
      System.out.printf("Analog value: %.04f\n", adc.getAnalog(channel));
    }
    adc.close();
    System.out.println("\nBye");
  }
}
