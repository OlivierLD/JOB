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

  // Main for tests
  public static void main(String... args) {
    String[] available = SPI.list();
//    System.out.println(String.format("Available: %s",
//      Arrays.stream(available)
//        .collect(Collectors.joining(", "))));
    System.out.printf("Available: %s\n", String.join(", ", available));
    MCP3008 adc = new MCP3008(SPI.list()[0]);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> go = false));
    while (go) {
      System.out.printf("Analog value: %.04f\n", adc.getAnalog(0));
    }
    adc.close();
    System.out.println("Bye");
  }
}
