package jhard.devices;

import jhard.io.SPI;

// LoRa!

/*
 * Wiring of the MCP3008-SPI:
 * +---------++------------------------------------------------------------------------------+
 * | RHRF95  || Raspberry PI                                                                 |
 * +---------++------+------------+---------+---------------+--------------------------------+
 * |         || Pin# | Name       | GPIO    | wiringPI/PI4J | Comments                       |
 * +---------++------+------------+---------+---------------+--------------------------------+
 * | Vin     ||   #1 | 3.3V       |         |               | or #17                         |
 * | GND     ||   #6 | GND        |         |               | or #9, #14, #20, #25, #34, #39 |
 * | CLK     ||  #23 | SPI0_CLK   | GPIO_11 |  14           |                                |
 * | MISO    ||  #21 | SPI0_MISO  | GPIO_9  |  13           |                                |
 * | MOSI    ||  #19 | SPI0_MOSI  | GPIO_10 |  12           |                                |
 * | CS      ||  #24 | SPI0_CE0_N | GPIO_8  |  10           |                                |
 * | RST     ||  #18 |            | GPIO_24 |  24           | or any pin available for dig out |
 * | G0(IRQ) ||   #7 |            | GPIO_4  |               |                                | // TODO Verify that one
 * +---------++------+------------+---------+---------------+--------------------------------+
 */

public class RHRF95 extends SPI {

  public RHRF95(String dev) {
    super(dev);
    super.settings(SPI.DEFAULT_SPEED, Endianness.LITTLE_ENDIAN, SPIMode.MODE0); // TASK MODE0 includes the RST?
  }
  // TODO The rest...
}
