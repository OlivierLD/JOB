package jhard.devices;

import jhard.io.I2C;
import utils.MiscUtils;
import utils.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static utils.MiscUtils.delay;

public class BME280 extends I2C {

	public final static int BME280_I2CADDR = 0x77;

	// Operating Modes
	public final static int BME280_OSAMPLE_1 = 1;
	public final static int BME280_OSAMPLE_2 = 2;
	public final static int BME280_OSAMPLE_4 = 3;
	public final static int BME280_OSAMPLE_8 = 4;
	public final static int BME280_OSAMPLE_16 = 5;

	// BME280 Registers
	public final static int BME280_REGISTER_DIG_T1 = 0x88;  // Trimming parameter registers
	public final static int BME280_REGISTER_DIG_T2 = 0x8A;
	public final static int BME280_REGISTER_DIG_T3 = 0x8C;

	public final static int BME280_REGISTER_DIG_P1 = 0x8E;
	public final static int BME280_REGISTER_DIG_P2 = 0x90;
	public final static int BME280_REGISTER_DIG_P3 = 0x92;
	public final static int BME280_REGISTER_DIG_P4 = 0x94;
	public final static int BME280_REGISTER_DIG_P5 = 0x96;
	public final static int BME280_REGISTER_DIG_P6 = 0x98;
	public final static int BME280_REGISTER_DIG_P7 = 0x9A;
	public final static int BME280_REGISTER_DIG_P8 = 0x9C;
	public final static int BME280_REGISTER_DIG_P9 = 0x9E;

	public final static int BME280_REGISTER_DIG_H1 = 0xA1;
	public final static int BME280_REGISTER_DIG_H2 = 0xE1;
	public final static int BME280_REGISTER_DIG_H3 = 0xE3;
	public final static int BME280_REGISTER_DIG_H4 = 0xE4;
	public final static int BME280_REGISTER_DIG_H5 = 0xE5;
	public final static int BME280_REGISTER_DIG_H6 = 0xE6;
	public final static int BME280_REGISTER_DIG_H7 = 0xE7;

	public final static int BME280_REGISTER_CHIPID = 0xD0;
	public final static int BME280_REGISTER_VERSION = 0xD1;
	public final static int BME280_REGISTER_SOFTRESET = 0xE0;

	public final static int BME280_REGISTER_CONTROL_HUM = 0xF2;
	public final static int BME280_REGISTER_CONTROL = 0xF4;
	public final static int BME280_REGISTER_CONFIG = 0xF5;
	public final static int BME280_REGISTER_PRESSURE_DATA = 0xF7;
	public final static int BME280_REGISTER_TEMP_DATA = 0xFA;
	public final static int BME280_REGISTER_HUMIDITY_DATA = 0xFD;

	private int dig_T1 = 0;
	private int dig_T2 = 0;
	private int dig_T3 = 0;

	private int dig_P1 = 0;
	private int dig_P2 = 0;
	private int dig_P3 = 0;
	private int dig_P4 = 0;
	private int dig_P5 = 0;
	private int dig_P6 = 0;
	private int dig_P7 = 0;
	private int dig_P8 = 0;
	private int dig_P9 = 0;

	private int dig_H1 = 0;
	private int dig_H2 = 0;
	private int dig_H3 = 0;
	private int dig_H4 = 0;
	private int dig_H5 = 0;
	private int dig_H6 = 0;

	private float tFine = 0F;

	public final static int DEFAULT_ADDR = BME280_I2CADDR;
	public final static String DEFAULT_BUS = "i2c-1";

	private int address;
	private int mode = BME280_OSAMPLE_8;

	private static boolean verbose = "true".equals(System.getProperty("bme280.verbose", "false"));

	public BME280() {
		this(DEFAULT_BUS, DEFAULT_ADDR);
	}
	public BME280(int addr) {
		this(DEFAULT_BUS, addr);
	}
	public BME280(String bus) {
		this(bus, DEFAULT_ADDR);
	}
	public BME280(String bus, int address) {
		super(bus);
		this.address = address;

		String[] deviceList = I2C.list();
		if (verbose) {
			System.out.println(String.format("Device list: %s",
				Arrays.asList(deviceList)
						.stream()
						.collect(Collectors.joining(", "))));
			System.out.println(String.format("Bus %s, address 0x%02X", bus, address));
		}

		// Soft reset
		this.command(BME280_REGISTER_SOFTRESET, (byte)0xB6);
		// Wait for the chip to wake up
		MiscUtils.delay(300L);

		try {
			this.readCalibrationData();
			if (verbose) {
				this.showCalibrationData();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		this.command(BME280_REGISTER_CONTROL, (byte)0x3F);
		tFine = 0.0f;
	}

	public void readCalibrationData() throws Exception {
		// Reads the calibration data from the IC
		dig_T1 = readU16LE(BME280_REGISTER_DIG_T1);
		dig_T2 = readS16LE(BME280_REGISTER_DIG_T2);
		dig_T3 = readS16LE(BME280_REGISTER_DIG_T3);

		dig_P1 = readU16LE(BME280_REGISTER_DIG_P1);
		dig_P2 = readS16LE(BME280_REGISTER_DIG_P2);
		dig_P3 = readS16LE(BME280_REGISTER_DIG_P3);
		dig_P4 = readS16LE(BME280_REGISTER_DIG_P4);
		dig_P5 = readS16LE(BME280_REGISTER_DIG_P5);
		dig_P6 = readS16LE(BME280_REGISTER_DIG_P6);
		dig_P7 = readS16LE(BME280_REGISTER_DIG_P7);
		dig_P8 = readS16LE(BME280_REGISTER_DIG_P8);
		dig_P9 = readS16LE(BME280_REGISTER_DIG_P9);

		dig_H1 = this.readU8(BME280_REGISTER_DIG_H1);
		dig_H2 = readS16LE(BME280_REGISTER_DIG_H2);
		dig_H3 = this.readU8(BME280_REGISTER_DIG_H3);
		dig_H6 = readS8(BME280_REGISTER_DIG_H7);

		int h4 = readS8(BME280_REGISTER_DIG_H4);
		h4 = (h4 << 24) >> 20;
		dig_H4 = h4 | (readU8(BME280_REGISTER_DIG_H5) & 0x0F);

		int h5 = readS8(BME280_REGISTER_DIG_H6);
		h5 = (h5 << 24) >> 20;
		dig_H5 = h5 | (readU8(BME280_REGISTER_DIG_H5) >> 4 & 0x0F);
	}

	private String displayRegister(int reg) {
		return String.format("0x%s (%d)", StringUtils.lpad(Integer.toHexString(reg & 0xFFFF).toUpperCase(), 4, "0"), reg);
	}

	private void showCalibrationData() {
		// Displays the calibration values for debugging purposes
		System.out.println("======================");
		System.out.println("DBG: T1 = " + displayRegister(dig_T1));
		System.out.println("DBG: T2 = " + displayRegister(dig_T2));
		System.out.println("DBG: T3 = " + displayRegister(dig_T3));
		System.out.println("----------------------");
		System.out.println("DBG: P1 = " + displayRegister(dig_P1));
		System.out.println("DBG: P2 = " + displayRegister(dig_P2));
		System.out.println("DBG: P3 = " + displayRegister(dig_P3));
		System.out.println("DBG: P4 = " + displayRegister(dig_P4));
		System.out.println("DBG: P5 = " + displayRegister(dig_P5));
		System.out.println("DBG: P6 = " + displayRegister(dig_P6));
		System.out.println("DBG: P7 = " + displayRegister(dig_P7));
		System.out.println("DBG: P8 = " + displayRegister(dig_P8));
		System.out.println("DBG: P9 = " + displayRegister(dig_P9));
		System.out.println("----------------------");
		System.out.println("DBG: H1 = " + displayRegister(dig_H1));
		System.out.println("DBG: H2 = " + displayRegister(dig_H2));
		System.out.println("DBG: H3 = " + displayRegister(dig_H3));
		System.out.println("DBG: H4 = " + displayRegister(dig_H4));
		System.out.println("DBG: H5 = " + displayRegister(dig_H5));
		System.out.println("DBG: H6 = " + displayRegister(dig_H6));
		System.out.println("======================");
	}

	private void command(int reg, byte val) {
		super.beginTransmission(this.address);
		super.write(reg);
		super.write(val);
		super.endTransmission();
	}

	private void command(int reg, byte[] val) {
		super.beginTransmission(this.address);
		super.write(reg);
		super.write(val);
		super.endTransmission();
	}

	private int readRawTemp() throws Exception {
		// Reads the raw (uncompensated) temperature from the sensor
		int meas = mode;
		if (verbose) {
			System.out.println(String.format("readRawTemp: 1 - meas=%d", meas));
		}
		this.command(BME280_REGISTER_CONTROL_HUM, (byte) meas); // HUM ?
		meas = mode << 5 | mode << 2 | 1;
		if (verbose) {
			System.out.println(String.format("readRawTemp: 2 - meas=%d", meas));
		}
		this.command(BME280_REGISTER_CONTROL, (byte) meas);

		double sleepTime = 0.00125 + 0.0023 * (1 << mode);
		sleepTime = sleepTime + 0.0023 * (1 << mode) + 0.000575;
		sleepTime = sleepTime + 0.0023 * (1 << mode) + 0.000575;
		delay((long) (sleepTime * 1_000L));
		int msb = this.readU8(BME280_REGISTER_TEMP_DATA);
		int lsb = this.readU8(BME280_REGISTER_TEMP_DATA + 1);
		int xlsb = this.readU8(BME280_REGISTER_TEMP_DATA + 2);
		int raw = ((msb << 16) | (lsb << 8) | xlsb) >> 4;
		if (verbose) {
			System.out.println("DBG: Raw Temp: " + (raw & 0xFFFF) + ", " + raw + String.format(", msb: 0x%04X lsb: 0x%04X xlsb: 0x%04X", msb, lsb, xlsb));
		}
		return raw;
	}

	private int readRawPressure() throws Exception {
		// Reads the raw (uncompensated) pressure level from the sensor
		int msb = this.readU8(BME280_REGISTER_PRESSURE_DATA);
		int lsb = this.readU8(BME280_REGISTER_PRESSURE_DATA + 1);
		int xlsb = this.readU8(BME280_REGISTER_PRESSURE_DATA + 2);
		int raw = ((msb << 16) | (lsb << 8) | xlsb) >> 4;
		return raw;
	}

	private int readRawHumidity() throws Exception {
		int msb = this.readU8(BME280_REGISTER_HUMIDITY_DATA);
		int lsb = this.readU8(BME280_REGISTER_HUMIDITY_DATA + 1);
		int raw = (msb << 8) | lsb;
		return raw;
	}

	public float readTemperature() throws Exception {
		// Gets the compensated temperature in degrees celcius
		float UT = readRawTemp();
		float var1 = 0;
		float var2 = 0;
		float temp = 0.0f;

		// Read raw temp before aligning it with the calibration values
		var1 = (UT / 16384.0f - dig_T1 / 1024.0f) * (float) dig_T2;
		var2 = ((UT / 131072.0f - dig_T1 / 8192.0f) * (UT / 131072.0f - dig_T1 / 8192.0f)) * (float) dig_T3;
		tFine = (int) (var1 + var2);
		temp = (var1 + var2) / 5120.0f;
		if (verbose) {
			System.out.println("DBG: Calibrated temperature = " + temp + " C");
		}
		return temp;
	}

	public float readPressure() throws Exception {
		// Gets the compensated pressure in pascal
		int adc = readRawPressure();
		if (verbose) {
			System.out.println("ADC:" + adc + ", tFine:" + tFine);
		}
		float var1 = (tFine / 2.0f) - 64000.0f;
		float var2 = var1 * var1 * (dig_P6 / 32768.0f);
		var2 = var2 + var1 * dig_P5 * 2.0f;
		var2 = (var2 / 4.0f) + (dig_P4 * 65536.0f);
		var1 = (dig_P3 * var1 * var1 / 524288.0f + dig_P2 * var1) / 524288.0f;
		var1 = (1.0f + var1 / 32768.0f) * dig_P1;
		if (var1 == 0f)
			return 0f;
		float p = 1048576.0f - adc;
		p = ((p - var2 / 4096.0f) * 6250.0f) / var1;
		var1 = dig_P9 * p * p / 2147483648.0f;
		var2 = p * dig_P8 / 32768.0f;
		p = p + (var1 + var2 + dig_P7) / 16.0f;
		if (verbose) {
			System.out.println("DBG: Pressure = " + p + " Pa");
		}
		return p;
	}

	public float readHumidity() throws Exception {
		int adc = readRawHumidity();
		float h = tFine - 76800.0f;
		h = (adc - (dig_H4 * 64.0f + dig_H5 / 16384.8f * h)) *
				(dig_H2 / 65536.0f * (1.0f + dig_H6 / 67108864.0f * h * (1.0f + dig_H3 / 67108864.0f * h)));
		h = h * (1.0f - dig_H1 * h / 524288.0f);
		if (h > 100)
			h = 100;
		else if (h < 0)
			h = 0;
		if (verbose) {
			System.out.println("DBG: Humidity = " + h);
		}
		return h;
	}

	private int standardSeaLevelPressure = 101_325; // in Pascals. 1013.25 hPa

	public void setStandardSeaLevelPressure(int standardSeaLevelPressure) {
		this.standardSeaLevelPressure = standardSeaLevelPressure;
	}

	public double readAltitude() throws Exception {
		// "Calculates the altitude in meters"
		double altitude = 0.0;
		float pressure = readPressure();
		altitude = 44330.0 * (1.0 - Math.pow(pressure / standardSeaLevelPressure, 0.1903));
		if (verbose) {
			System.out.println("DBG: Altitude = " + altitude);
		}
		return altitude;
	}

	public int readU16LE(int register) {
		System.out.println(String.format("Java >> readU16LE, - 1, beginTx at 0x%02X", this.address));
		super.beginTransmission(this.address);
		System.out.println(String.format("Java >> readU16LE, - 2, write at reg 0x%02X", register));
		super.write((byte)register);
//		try {
//			System.out.println("Java >> endTx");
//			super.endTransmission();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		System.out.println(String.format("Java >> readU16LE, - 3, now reading %d byte(s)", 2));
		byte[] ba = super.read(2);

		System.out.println(String.format("Java >> Read %s byte(s)", (ba == null ? "NULL" : String.format("%d", ba.length))));

		try {
			System.out.println("Java >> endTx");
			super.endTransmission();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ((ba[1] & 0xFF) << 8) + (ba[0] & 0xFF); // Little Endian
	}

	public int readS16LE(int register) {
		super.beginTransmission(this.address);
		super.write((byte)register);
		byte[] ba = super.read(2);
		super.endTransmission();

		int lo = ba[0] & 0xFF;
		int hi = ba[1] & 0xFF;
		if (hi > 127)
			hi -= 256;
		return (hi << 8) + lo; // Little Endian
	}

	public int readU8(int register) {
		super.beginTransmission(this.address);
		super.write(register);
		byte[] ba = super.read(1);
		super.endTransmission();
		return (int)(ba[0] & 0xFF);
	}

	public int readS8(int register) {
		int val = this.readU8(register);
		if (val > 127)
			val -= 256;
		return val;
	}
}
