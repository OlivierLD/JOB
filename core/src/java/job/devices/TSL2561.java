package job.devices;

import job.io.I2C;
import utils.MiscUtils;

// TSL2561 is light sensor. Big Endian.
// datasheet: https://cdn-shop.adafruit.com/datasheets/TSL2561.pdf

public class TSL2561 extends I2C {
	public final static int TSL2561_ADDRESS = 0x39;
	public final static String DEFAULT_BUS = "i2c-1";

	public final static int TSL2561_ADDRESS_LOW = 0x29;
	public final static int TSL2561_ADDRESS_FLOAT = 0x39;
	public final static int TSL2561_ADDRESS_HIGH = 0x49;

	public final static int TSL2561_COMMAND_BIT = 0x80;
	public final static int TSL2561_WORD_BIT = 0x20;
	public final static int TSL2561_CONTROL_POWERON = 0x03;
	public final static int TSL2561_CONTROL_POWEROFF = 0x00;

	public final static int TSL2561_REGISTER_CONTROL = 0x00;
	public final static int TSL2561_REGISTER_TIMING = 0x01;
	public final static int TSL2561_REGISTER_CHAN0_LOW = 0x0C;
	public final static int TSL2561_REGISTER_CHAN0_HIGH = 0x0D;
	public final static int TSL2561_REGISTER_CHAN1_LOW = 0x0E;
	public final static int TSL2561_REGISTER_CHAN1_HIGH = 0x0F;
	public final static int TSL2561_REGISTER_ID = 0x0A;

	public final static int TSL2561_GAIN_1X = 0x00;
	public final static int TSL2561_GAIN_16X = 0x10;

	public final static int TSL2561_INTEGRATIONTIME_13MS = 0x00; // rather 13.7ms
	public final static int TSL2561_INTEGRATIONTIME_101MS = 0x01;
	public final static int TSL2561_INTEGRATIONTIME_402MS = 0x02;

	public final static double TSL2561_LUX_K1C = 0.130;   // (0x0043)  // 0.130 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B1C = 0.0315;  // (0x0204)  // 0.0315 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M1C = 0.0262;  // (0x01ad)  // 0.0262 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K2C = 0.260;   // (0x0085)  // 0.260 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B2C = 0.0337;  // (0x0228)  // 0.0337 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M2C = 0.0430;  // (0x02c1)  // 0.0430 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K3C = 0.390;   // (0x00c8)  // 0.390 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B3C = 0.0363;  // (0x0253)  // 0.0363 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M3C = 0.0529;  // (0x0363)  // 0.0529 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K4C = 0.520;   // (0x010a)  // 0.520 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B4C = 0.0392;  // (0x0282)  // 0.0392 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M4C = 0.0605;  // (0x03df)  // 0.0605 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K5C = 0.65;    // (0x014d)  // 0.65 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B5C = 0.0229;  // (0x0177)  // 0.0229 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M5C = 0.0291;  // (0x01dd)  // 0.0291 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K6C = 0.80;    // (0x019a)  // 0.80 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B6C = 0.0157;  // (0x0101)  // 0.0157 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M6C = 0.0180;  // (0x0127)  // 0.0180 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K7C = 1.3;     // (0x029a)  // 1.3 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B7C = 0.00338; // (0x0037)  // 0.00338 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M7C = 0.00260; // (0x002b)  // 0.00260 * 2^LUX_SCALE
	public final static double TSL2561_LUX_K8C = 1.3;     // (0x029a)  // 1.3 * 2^RATIO_SCALE
	public final static double TSL2561_LUX_B8C = 0.000;   // (0x0000)  // 0.000 * 2^LUX_SCALE
	public final static double TSL2561_LUX_M8C = 0.000;   // (0x0000)  // 0.000 * 2^LUX_SCALE

	private final static boolean VERBOSE = "true".equals(System.getProperty("tls2561.verbose"));
	private int gain = TSL2561_GAIN_1X;
	private int integration = TSL2561_INTEGRATIONTIME_402MS;
	private final int pause = 800;

	private final int address;

	public TSL2561() {
		this(DEFAULT_BUS, TSL2561_ADDRESS);
	}

	public TSL2561(int addr) {
		this(DEFAULT_BUS, addr);
	}

	public TSL2561(String bus) {
		this(bus, TSL2561_ADDRESS);
	}

	public TSL2561(String bus, int address) {
		super(bus);
		this.address = address;

		String[] deviceList = I2C.list();
		if (VERBOSE) {
			StringBuilder sb = new StringBuilder();
			for (String device : deviceList) {
				sb.append((sb.length() > 0 ? ", " : "") + device);
			}
			System.out.printf("Device list: %s\n", sb.toString());
			System.out.printf("Bus %s, address 0x%02X\n", bus, address);
		}
		turnOn();
	}

	public void turnOn() {
		command(TSL2561_COMMAND_BIT, (byte) TSL2561_CONTROL_POWERON);
	}

	public void turnOff() {
		command(TSL2561_COMMAND_BIT, (byte) TSL2561_CONTROL_POWEROFF);
	}

	public void setGain() {
		setGain(TSL2561_GAIN_1X);
	}

	public void setGain(int gain) {
		setGain(gain, TSL2561_INTEGRATIONTIME_402MS);
	}

	public void setGain(int gain, int integration) {
		if (gain != TSL2561_GAIN_1X && gain != TSL2561_GAIN_16X) {
			throw new IllegalArgumentException("Bad  gain value [" + gain + "]");
		}
		if (gain != this.gain || integration != this.integration) {
			command(TSL2561_COMMAND_BIT | TSL2561_REGISTER_TIMING, (byte) (gain | integration));
			if (VERBOSE) {
				System.out.println("Setting low gain");
			}
			this.gain = gain;
			this.integration = integration;
			MiscUtils.delay(pause); // pause for integration (pause must be bigger than integration time)
		}
	}

	/*
	 * Reads visible+IR diode from the I2C device
	 */
	public int readFull() {
		int reg = TSL2561_COMMAND_BIT | TSL2561_REGISTER_CHAN0_LOW;
		return readU16(reg);
	}

	/*
	 * Reads IR only diode from the I2C device
	 */
	public int readIR() {
		int reg = TSL2561_COMMAND_BIT | TSL2561_REGISTER_CHAN1_LOW;
		return readU16(reg);
	}

	/*
	 * Device lux range 0.1 - 40,000+
	 * see https://learn.adafruit.com/tsl2561/overview
	 */
	public double readLux() {
		int ambient = this.readFull();
		int ir = this.readIR();

		if (ambient >= 0xffff || ir >= 0xffff) { // value(s) exeed(s) datarange
			throw new RuntimeException("Gain too high. Values exceed range.");
		}
		if (false && this.gain == TSL2561_GAIN_1X) {
			ambient *= 16;    // scale 1x to 16x
			ir *= 16;         // scale 1x to 16x
		}
		double ratio = (ir / (float) ambient);

		if (VERBOSE) {
			System.out.println("IR Result:" + ir);
			System.out.println("Ambient Result:" + ambient);
		}
		/*
		 * For the values below, see https://github.com/adafruit/_TSL2561/blob/master/_TSL2561_U.h
		 */
		double lux = 0d;
		if ((ratio >= 0) && (ratio <= TSL2561_LUX_K4C)) {
			lux = (TSL2561_LUX_B1C * ambient) - (0.0593 * ambient * (Math.pow(ratio, 1.4)));
		} else if (ratio <= TSL2561_LUX_K5C) {
			lux = (TSL2561_LUX_B5C * ambient) - (TSL2561_LUX_M5C * ir);
		} else if (ratio <= TSL2561_LUX_K6C) {
			lux = (TSL2561_LUX_B6C * ambient) - (TSL2561_LUX_M6C * ir);
		} else if (ratio <= TSL2561_LUX_K7C) {
			lux = (TSL2561_LUX_B7C * ambient) - (TSL2561_LUX_M7C * ir);
		} else if (ratio > TSL2561_LUX_K8C) {
			lux = 0;
		}
		return lux;
	}

	private void command(int reg, byte val) {
		super.beginTransmission(this.address);
		super.write(reg);
		super.write(val);
		super.endTransmission();
	}

	/*
	 * Read an unsigned byte from the I2C device
	 */
	/**
	 * Read an Unsigned byte from register.
	 * @param register register
	 * @return unsigned 8-bit int
	 */
	private int readU8(int register) {
		super.beginTransmission(this.address);
		super.write(register);
		byte[] ba = super.read(1);
		super.endTransmission();
		return (int)(ba[0] & 0xFF);
	}

	private int readU16(int register) {
		int lo = this.readU8(register);
		int hi = this.readU8(register + 1);
		int result = (hi << 8) + lo; // Big Endian
		if (VERBOSE) {
			System.out.println("(U16) I2C: Device " + toHex(TSL2561_ADDRESS) + " returned " + toHex(result) + " from reg " + toHex(register));
		}
		return result;
	}

	private String toHex(int i) {
		String s = Integer.toString(i, 16).toUpperCase();
		while (s.length() % 2 != 0) {
			s = "0" + s;
		}
		return "0x" + s;
	}
}
