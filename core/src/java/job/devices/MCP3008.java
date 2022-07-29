package job.devices;

import job.io.SPI;

// MCP3008 is an Analog-to-Digital Converter using SPI.
// It has 8 input channels.
// datasheet: http://ww1.microchip.com/downloads/en/DeviceDoc/21295d.pdf

/*
 * Master is the Raspberry Pi, MCP3008 is the slave.
 * Wiring of the MCP3008-SPI (without power supply):
 * +---------++---------------------------------------------+
 * | MCP3008 || Raspberry PI                                |
 * +---------++------+------------+---------+---------------+
 * |         || Pin# | Name       | GPIO    | wiringPI/PI4J |
 * +---------++------+------------+---------+---------------+
 * | CLK     ||  #23 | SPI0_CLK   | GPIO_11 |  14           |
 * | Dout    ||  #21 | SPI0_MISO  | GPIO_9  |  13           |
 * | Din     ||  #19 | SPI0_MOSI  | GPIO_10 |  12           |
 * | CS      ||  #24 | SPI0_CE0_N | GPIO_8  |  10           |
 * +---------++------+------------+---------+---------------+
 *
 * TODO: How to change the pins wiring?
 */

public class MCP3008 extends SPI {

	public MCP3008(String dev) {
		super(dev);
		settings(SPI.DEFAULT_SPEED, Endianness.LITTLE_ENDIAN, SPIMode.MODE0);
	}

	public float getAnalog(int channel) {
		if (channel < 0 || channel > 7) {
			System.err.println("Channel must be in [0..7]");
			throw new IllegalArgumentException("Unexpected channel");
		}
		byte[] out = {0, 0, 0};
		// encode the channel number in the first byte
		out[0] = (byte) (0x18 | channel);
		byte[] in = transfer(out);
		int val = ((in[1] & 0x03) << 8) | (in[2] & 0xff); // val is between 0 and 1023
		return val / 1023.0f; // translated to [0..1]
	}
}

