import jhard.io.SPI;
import java.util.Arrays;
import java.util.stream.Collectors;

// MCP3008 is a Analog-to-Digital converter using SPI
// that has 8 input channels
// datasheet: http://ww1.microchip.com/downloads/en/DeviceDoc/21295d.pdf

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

public class MCP3008 extends SPI {

  public MCP3008(String dev) {
    super(dev);
    settings(500_000, SPI.MSBFIRST, SPI.MODE0);
  }

  public float getAnalog(int channel) {
    if (channel < 0 ||  7 < channel) {
      System.err.println("The channel needs to be from 0 to 7");
      throw new IllegalArgumentException("Unexpected channel");
    }
    byte[] out = { 0, 0, 0 };
    // encode the channel number in the first byte
    out[0] = (byte)(0x18 | channel);
    byte[] in = transfer(out);
    int val = ((in[1] & 0x03) << 8) | (in[2] & 0xff);
    // val is between 0 and 1023
    return val/1023.0f;
  }

  private static boolean go = true;
  // Main for tests
  public static void main(String... args) {
    String available[] = SPI.list();
    System.out.println(String.format("Available: %s",
      Arrays.asList(available)
        .stream()
        .collect(Collectors.joining(", "))));
    MCP3008 adc = new MCP3008(SPI.list()[0]);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      go = false;
    }));
    while (go) {
      System.out.println(String.format("Analog value: %.04f", adc.getAnalog(0)));
    }
    adc.close();
    System.out.println("Bye");
  }
}
