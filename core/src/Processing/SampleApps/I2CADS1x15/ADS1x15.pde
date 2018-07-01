import processing.io.I2C;

/**
 * ADS1015 and ADS1115.
 * Both share the same address.
 * I2C Interface ADC
 */
public class ADS1x15 extends I2C {
  boolean verbose = ("true".equals(System.getProperty("ads1x.verbose", "false")));

  final static String DEFAULT_BUS = "i2c-1";

  private int address;
  private final static int ADS1x15_ADDRESS = 0x48;
  // Pointer Register
  public final static int ADS1015_REG_POINTER_MASK = 0x03;
  public final static int ADS1015_REG_POINTER_CONVERT = 0x00;
  public final static int ADS1015_REG_POINTER_CONFIG = 0x01;
  public final static int ADS1015_REG_POINTER_LOWTHRESH = 0x02;
  public final static int ADS1015_REG_POINTER_HITHRESH = 0x03;

  // Config Register
  public final static int ADS1015_REG_CONFIG_OS_MASK = 0x8000;
  public final static int ADS1015_REG_CONFIG_OS_SINGLE = 0x8000;  // Write: Set to start a single-conversion
  public final static int ADS1015_REG_CONFIG_OS_BUSY = 0x0000;  // Read: Bit = 0 when conversion is in progress
  public final static int ADS1015_REG_CONFIG_OS_NOTBUSY = 0x8000;  // Read: Bit = 1 when device is not performing a conversion

  public final static int ADS1015_REG_CONFIG_MUX_MASK = 0x7000;
  public final static int ADS1015_REG_CONFIG_MUX_DIFF_0_1 = 0x0000;  // Differential P = AIN0, N = AIN1 (default)
  public final static int ADS1015_REG_CONFIG_MUX_DIFF_0_3 = 0x1000;  // Differential P = AIN0, N = AIN3
  public final static int ADS1015_REG_CONFIG_MUX_DIFF_1_3 = 0x2000;  // Differential P = AIN1, N = AIN3
  public final static int ADS1015_REG_CONFIG_MUX_DIFF_2_3 = 0x3000;  // Differential P = AIN2, N = AIN3
  public final static int ADS1015_REG_CONFIG_MUX_SINGLE_0 = 0x4000;  // Single-ended AIN0
  public final static int ADS1015_REG_CONFIG_MUX_SINGLE_1 = 0x5000;  // Single-ended AIN1
  public final static int ADS1015_REG_CONFIG_MUX_SINGLE_2 = 0x6000;  // Single-ended AIN2
  public final static int ADS1015_REG_CONFIG_MUX_SINGLE_3 = 0x7000;  // Single-ended AIN3

  public final static int ADS1015_REG_CONFIG_PGA_MASK = 0x0E00;
  public final static int ADS1015_REG_CONFIG_PGA_6_144V = 0x0000;  // +/-6.144V range
  public final static int ADS1015_REG_CONFIG_PGA_4_096V = 0x0200;  // +/-4.096V range
  public final static int ADS1015_REG_CONFIG_PGA_2_048V = 0x0400;  // +/-2.048V range (default)
  public final static int ADS1015_REG_CONFIG_PGA_1_024V = 0x0600;  // +/-1.024V range
  public final static int ADS1015_REG_CONFIG_PGA_0_512V = 0x0800;  // +/-0.512V range
  public final static int ADS1015_REG_CONFIG_PGA_0_256V = 0x0A00;  // +/-0.256V range

  public final static int ADS1015_REG_CONFIG_MODE_MASK = 0x0100;
  public final static int ADS1015_REG_CONFIG_MODE_CONTIN = 0x0000;  // Continuous conversion mode
  public final static int ADS1015_REG_CONFIG_MODE_SINGLE = 0x0100;  // Power-down single-shot mode (default)

  public final static int ADS1015_REG_CONFIG_DR_MASK = 0x00E0;
  public final static int ADS1015_REG_CONFIG_DR_128SPS = 0x0000;  // 128 samples per second
  public final static int ADS1015_REG_CONFIG_DR_250SPS = 0x0020;  // 250 samples per second
  public final static int ADS1015_REG_CONFIG_DR_490SPS = 0x0040;  // 490 samples per second
  public final static int ADS1015_REG_CONFIG_DR_920SPS = 0x0060;  // 920 samples per second
  public final static int ADS1015_REG_CONFIG_DR_1600SPS = 0x0080;  // 1600 samples per second (default)
  public final static int ADS1015_REG_CONFIG_DR_2400SPS = 0x00A0;  // 2400 samples per second
  public final static int ADS1015_REG_CONFIG_DR_3300SPS = 0x00C0;  // 3300 samples per second (also 0x00E0)

  public final static int ADS1115_REG_CONFIG_DR_8SPS = 0x0000;  // 8 samples per second
  public final static int ADS1115_REG_CONFIG_DR_16SPS = 0x0020;  // 16 samples per second
  public final static int ADS1115_REG_CONFIG_DR_32SPS = 0x0040;  // 32 samples per second
  public final static int ADS1115_REG_CONFIG_DR_64SPS = 0x0060;  // 64 samples per second
  public final static int ADS1115_REG_CONFIG_DR_128SPS = 0x0080;  // 128 samples per second
  public final static int ADS1115_REG_CONFIG_DR_250SPS = 0x00A0;  // 250 samples per second (default)
  public final static int ADS1115_REG_CONFIG_DR_475SPS = 0x00C0;  // 475 samples per second
  public final static int ADS1115_REG_CONFIG_DR_860SPS = 0x00E0;  // 860 samples per second

  public final static int ADS1015_REG_CONFIG_CMODE_MASK = 0x0010;
  public final static int ADS1015_REG_CONFIG_CMODE_TRAD = 0x0000;  // Traditional comparator with hysteresis (default)
  public final static int ADS1015_REG_CONFIG_CMODE_WINDOW = 0x0010;  // Window comparator

  public final static int ADS1015_REG_CONFIG_CPOL_MASK = 0x0008;
  public final static int ADS1015_REG_CONFIG_CPOL_ACTVLOW = 0x0000;  // ALERT/RDY pin is low when active (default)
  public final static int ADS1015_REG_CONFIG_CPOL_ACTVHI = 0x0008;  // ALERT/RDY pin is high when active

  public final static int ADS1015_REG_CONFIG_CLAT_MASK = 0x0004;  // Determines if ALERT/RDY pin latches once asserted
  public final static int ADS1015_REG_CONFIG_CLAT_NONLAT = 0x0000;  // Non-latching comparator (default)
  public final static int ADS1015_REG_CONFIG_CLAT_LATCH = 0x0004;  // Latching comparator

  public final static int ADS1015_REG_CONFIG_CQUE_MASK = 0x0003;
  public final static int ADS1015_REG_CONFIG_CQUE_1CONV = 0x0000;  // Assert ALERT/RDY after one conversions
  public final static int ADS1015_REG_CONFIG_CQUE_2CONV = 0x0001;  // Assert ALERT/RDY after two conversions
  public final static int ADS1015_REG_CONFIG_CQUE_4CONV = 0x0002;  // Assert ALERT/RDY after four conversions
  public final static int ADS1015_REG_CONFIG_CQUE_NONE = 0x0003;  // Disable the comparator and put ALERT/RDY in high state (default)


  private ICType adcType;
  private int pga;

  public ADS1x15() {
    this(ICType.IC_ADS1015);
  }

  public ADS1x15(int address) {
    this(DEFAULT_BUS, ICType.IC_ADS1015, address);
  }

  public ADS1x15(ICType icType) {
    this(DEFAULT_BUS, icType, ADS1x15_ADDRESS);
  }

  public ADS1x15(String bus, ICType icType, int address) {
    super(bus);
    this.address = address;
    this.adcType = icType;

    String[] deviceList = I2C.list();
    if (verbose) {
      StringBuffer sb = new StringBuffer();
      for (String device : deviceList) {
        sb.append((sb.length() > 0 ? ", " : "") + device);
      }
      println(String.format("Device list: %s", sb.toString()));
      println(String.format("Bus %s, address 0x%02X", bus, address));
    }

    // Set pga value, so that getLastConversionResult() can use it,
    // any function that accepts a pga value must update this.
    this.pga = pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning();
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

  void delay(double delay) {
    long ms = (long) Math.floor(delay);
    int ns = (int) ((delay - ms) * 1E6);
//  println("Delay:" + delay + " ms:" + ms + ", ns:" + ns);
    try {
      Thread.sleep(ms, ns);
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
  }

  public float readADCSingleEnded() {
    return readADCSingleEnded(Channels.CHANNEL_0);
  }

  public float readADCSingleEnded(Channels channel) {
    return readADCSingleEnded(channel,
        pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(),
        spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Gets a single-ended ADC reading from the specified channel in mV.
   * The sample rate for this mode (single-shot) can be used to lower the noise
   * (low sps) or to lower the power consumption (high sps) by duty cycling,
   * see datasheet page 14 for more info.
   * The pga must be given in mV, see page 13 for the supported values.
   *
   * @param channel 0-3
   * @param pga
   * @param sps     Samples per second
   * @return
   */
  public float readADCSingleEnded(Channels channel, int pga, int sps) {
    // Disable comparator, Non-latching, Alert/Rdy active low
    // traditional comparator, single-shot mode
    int config = ADS1015_REG_CONFIG_CQUE_NONE |
        ADS1015_REG_CONFIG_CLAT_NONLAT |
        ADS1015_REG_CONFIG_CPOL_ACTVLOW |
        ADS1015_REG_CONFIG_CMODE_TRAD |
        ADS1015_REG_CONFIG_MODE_SINGLE;

    // Set sample per seconds, defaults to 250sps
    // If sps is in the dictionary (defined in init) it returns the value of the constant
    // otherwise it returns the value for 250sps. This saves a lot of if/elif/else code!
    if (this.adcType.equals(ICType.IC_ADS1015)) {
      config |= spsADS1015.setDefault(sps, ADS1015_REG_CONFIG_DR_1600SPS);
    } else {
      config |= spsADS1115.setDefault(sps, ADS1115_REG_CONFIG_DR_250SPS);
    }
    // Set PGA/voltage range, defaults to +-6.144V
    config |= pgaADS1x15.setDefault(pga, ADS1015_REG_CONFIG_PGA_6_144V);
    this.pga = pga;

    // Set the channel to be converted
    if (channel == Channels.CHANNEL_3) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_3;
    } else if (channel == Channels.CHANNEL_2) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_2;
    } else if (channel == Channels.CHANNEL_1) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_1;
    } else {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_0;
    }

    // Set 'start single-conversion' bit
    config |= ADS1015_REG_CONFIG_OS_SINGLE;

    // Write config register to the ADC
    byte[] bytes = {(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};
    this.command(ADS1015_REG_POINTER_CONFIG, bytes);

    // Wait for the ADC conversion to complete
    // The minimum delay depends on the sps: delay >= 1/sps
    // We add 0.1ms to be sure
    double delay = ((1000 / sps) + 0.1);
    delay(delay);

    // Read the conversion results
    return readConversionResult();
  }

  public float readADCDifferential() {
    return readADCDifferential(Channels.CHANNEL_0, Channels.CHANNEL_1, pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Gets a differential ADC reading from channels chP and chN in mV.
   * The sample rate for this mode (single-shot) can be used to lower the noise
   * (low sps) or to lower the power consumption (high sps) by duty cycling,
   * see data sheet page 14 for more info.
   * The pga must be given in mV, see page 13 for the supported values.
   */
  public float readADCDifferential(Channels chP, Channels chN, int pga, int sps) {
    // Disable comparator, Non-latching, Alert/Rdy active low
    // traditional comparator, single-shot mode
    int config = ADS1015_REG_CONFIG_CQUE_NONE |
        ADS1015_REG_CONFIG_CLAT_NONLAT |
        ADS1015_REG_CONFIG_CPOL_ACTVLOW |
        ADS1015_REG_CONFIG_CMODE_TRAD |
        ADS1015_REG_CONFIG_MODE_SINGLE;

    // Set channels
    if ((chP == Channels.CHANNEL_0) && (chN == Channels.CHANNEL_1)) {    // 0 1
      config |= ADS1015_REG_CONFIG_MUX_DIFF_0_1;
    } else if ((chP == Channels.CHANNEL_0) & (chN == Channels.CHANNEL_3)) { // 0 3
      config |= ADS1015_REG_CONFIG_MUX_DIFF_0_3;
    } else if ((chP == Channels.CHANNEL_2) & (chN == Channels.CHANNEL_3)) { // 2 3
      config |= ADS1015_REG_CONFIG_MUX_DIFF_2_3;
    } else if ((chP == Channels.CHANNEL_1) & (chN == Channels.CHANNEL_3)) { // 1 3
      config |= ADS1015_REG_CONFIG_MUX_DIFF_1_3;
    } else {
      if (verbose) {
        System.out.printf("ADS1x15: Invalid channels specified: %d, %d\n", chP, chN);
        return -1;
      }
    }
    // Set sample per seconds, defaults to 250sps
    // If sps is in the dictionary (defined in init()) it returns the value of the constant
    // othewise it returns the value for 250sps. This saves a lot of if/elif/else code!
    if (this.adcType == ICType.IC_ADS1015) {
      config |= spsADS1015.setDefault(sps, ADS1015_REG_CONFIG_DR_1600SPS);
    } else {
      config |= spsADS1115.setDefault(sps, ADS1115_REG_CONFIG_DR_250SPS);
    }
    // Set PGA/voltage range, defaults to +-6.144V
    config |= pgaADS1x15.setDefault(pga, ADS1015_REG_CONFIG_PGA_6_144V);
    this.pga = pga;

    // Set 'start single-conversion' bit
    config |= ADS1015_REG_CONFIG_OS_SINGLE;

    // Write config register to the ADC
    byte[] bytes = {(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};

    this.command(ADS1015_REG_POINTER_CONFIG, bytes);

    // Wait for the ADC conversion to complete
    // The minimum delay depends on the sps: delay >= 1/sps
    // We add 0.1ms to be sure
    double delay = ((1000 / sps) + 0.1);
    delay(delay);

    // Read the conversion results
    return readConversionResult();
  }

  public float readADCDifferential01() {
    return readADCDifferential01(pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Gets a differential ADC reading from channels 0 and 1 in mV
   * The sample rate for this mode (single-shot) can be used to lower the noise
   * (low sps) or to lower the power consumption (high sps) by duty cycling,
   * see data sheet page 14 for more info.
   * The pga must be given in mV, see page 13 for the supported values.
   */
  public float readADCDifferential01(int pga, int sps) {
    return readADCDifferential(Channels.CHANNEL_0, Channels.CHANNEL_1, pga, sps);
  }

  public float readADCDifferential03() {
    return readADCDifferential03(pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Gets a differential ADC reading from channels 0 and 3 in mV
   * The sample rate for this mode (single-shot) can be used to lower the noise
   * (low sps) or to lower the power consumption (high sps) by duty cycling,
   * see data sheet page 14 for more info.
   * The pga must be given in mV, see page 13 for the supported values.
   */
  public float readADCDifferential03(int pga, int sps) {
    return readADCDifferential(Channels.CHANNEL_0, Channels.CHANNEL_3, pga, sps);
  }

  public float readADCDifferential13() {
    return readADCDifferential13(pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Gets a differential ADC reading from channels 1 and 3 in mV
   * The sample rate for this mode (single-shot) can be used to lower the noise
   * (low sps) or to lower the power consumption (high sps) by duty cycling,
   * see data sheet page 14 for more info.
   * The pga must be given in mV, see page 13 for the supported values.
   */
  public float readADCDifferential13(int pga, int sps) {
    return readADCDifferential(Channels.CHANNEL_1, Channels.CHANNEL_3, pga, sps);
  }

  public float readADCDifferential23() {
    return readADCDifferential23(pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Gets a differential ADC reading from channels 2 and 3 in mV
   * The sample rate for this mode (single-shot) can be used to lower the noise
   * (low sps) or to lower the power consumption (high sps) by duty cycling,
   * see data sheet page 14 for more info.
   * The pga must be given in mV, see page 13 for the supported values.
   */
  public float readADCDifferential23(int pga, int sps) {
    return readADCDifferential(Channels.CHANNEL_2, Channels.CHANNEL_3, pga, sps);
  }

  public float startContinuousConversion() {
    return startContinuousConversion(Channels.CHANNEL_0);
  }

  public float startContinuousConversion(Channels channel) {
    return startContinuousConversion(channel, pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Starts the continuous conversion mode and returns the first ADC reading
   * in mV from the specified channel.
   * The sps controls the sample rate.
   * The pga must be given in mV, see datasheet page 13 for the supported values.
   * Use getLastConversionResults() to read the next values and
   * stopContinuousConversion() to stop converting.
   */
  public float startContinuousConversion(Channels channel, int pga, int sps) {
    // Disable comparator, Non-latching, Alert/Rdy active low
    // traditional comparator, continuous mode
    // The last flag is the only change we need, page 11 datasheet
    int config = ADS1015_REG_CONFIG_CQUE_NONE |
        ADS1015_REG_CONFIG_CLAT_NONLAT |
        ADS1015_REG_CONFIG_CPOL_ACTVLOW |
        ADS1015_REG_CONFIG_CMODE_TRAD |
        ADS1015_REG_CONFIG_MODE_CONTIN;

    // Set sample per seconds, defaults to 250sps
    // If sps is in the dictionary (defined in init()) it returns the value of the constant
    // othewise it returns the value for 250sps. This saves a lot of if/elif/else code!
    if (this.adcType == ICType.IC_ADS1015) {
      config |= spsADS1015.setDefault(sps, ADS1015_REG_CONFIG_DR_1600SPS);
    } else {
      config |= spsADS1115.setDefault(sps, ADS1115_REG_CONFIG_DR_250SPS);
    }

    // Set PGA/voltage range, defaults to +-6.144V
    config |= pgaADS1x15.setDefault(pga, ADS1015_REG_CONFIG_PGA_6_144V);
    this.pga = pga;

    // Set the channel to be converted
    if (channel == Channels.CHANNEL_3) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_3;
    } else if (channel == Channels.CHANNEL_2) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_2;
    } else if (channel == Channels.CHANNEL_1) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_1;
    } else {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_0;
    }
    // Set 'start single-conversion' bit to begin conversions
    // No need to change this for continuous mode!
    config |= ADS1015_REG_CONFIG_OS_SINGLE;

    // Write config register to the ADC
    // Once we write the ADC will convert continously
    // we can read the next values using getLastConversionResult
    byte[] bytes = {(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};
    this.command(ADS1015_REG_POINTER_CONFIG, bytes);

    // Wait for the ADC conversion to complete
    // The minimum delay depends on the sps: delay >= 1/sps
    // We add 0.5ms to be sure
    double delay = ((1000 / sps) + 0.5);
    delay(delay);

    // Read the conversion results
    return readConversionResult();
  }

  public float startContinuousDifferentialConversion() {
    return startContinuousDifferentialConversion(Channels.CHANNEL_0, Channels.CHANNEL_1);
  }

  public float startContinuousDifferentialConversion(Channels chP, Channels chN) {
    return startContinuousDifferentialConversion(chP, chN, pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  /**
   * Starts the continuous differential conversion mode and returns the first ADC reading
   * in mV as the difference from the specified channels.
   * The sps controls the sample rate.
   * The pga must be given in mV, see datasheet page 13 for the supported values.
   * Use getLastConversionResults() to read the next values and
   * stopContinuousConversion() to stop converting.
   */
  public float startContinuousDifferentialConversion(Channels chP, Channels chN, int pga, int sps) {
    // Disable comparator, Non-latching, Alert/Rdy active low
    // traditional comparator, continuous mode
    // The last flag is the only change we need, page 11 datasheet
    int config = ADS1015_REG_CONFIG_CQUE_NONE |
        ADS1015_REG_CONFIG_CLAT_NONLAT |
        ADS1015_REG_CONFIG_CPOL_ACTVLOW |
        ADS1015_REG_CONFIG_CMODE_TRAD |
        ADS1015_REG_CONFIG_MODE_CONTIN;

    // Set sample per seconds, defaults to 250sps
    // If sps is in the dictionary (defined in init()) it returns the value of the constant
    // othewise it returns the value for 250sps. This saves a lot of if/elif/else code!
    if (this.adcType == ICType.IC_ADS1015) {
      config |= spsADS1015.setDefault(sps, ADS1015_REG_CONFIG_DR_1600SPS);
    } else {
      config |= spsADS1115.setDefault(sps, ADS1115_REG_CONFIG_DR_250SPS);
    }
    // Set PGA/voltage range, defaults to +-6.144V
    config |= pgaADS1x15.setDefault(pga, ADS1015_REG_CONFIG_PGA_6_144V);
    this.pga = pga;

    // Set channels
    if ((chP == Channels.CHANNEL_0) & (chN == Channels.CHANNEL_1)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_0_1;
    } else if ((chP == Channels.CHANNEL_0) & (chN == Channels.CHANNEL_3)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_0_3;
    } else if ((chP == Channels.CHANNEL_2) & (chN == Channels.CHANNEL_3)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_2_3;
    } else if ((chP == Channels.CHANNEL_1) & (chN == Channels.CHANNEL_3)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_1_3;
    } else {
      if (verbose) {
        System.out.printf("ADS1x15: Invalid channels specified: %d, %d", chP, chN);
      }
      return -1;
    }
    // Set 'start single-conversion' bit to begin conversions
    // No need to change this for continuous mode!
    config |= ADS1015_REG_CONFIG_OS_SINGLE;

    // Write config register to the ADC
    // Once we write the ADC will convert continously
    // we can read the next values using getLastConversionResult
    byte[] bytes = {(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};
    this.command(ADS1015_REG_POINTER_CONFIG, bytes);

    // Wait for the ADC conversion to complete
    // The minimum delay depends on the sps: delay >= 1/sps
    // We add 0.5ms to be sure
    double delay = ((1000 / sps) + 0.5);
    delay(delay);

    // Read the conversion results
    return readConversionResult();
  }

  /**
   * Stops the ADC's conversions when in continuous mode
   * and resets the configuration to its default value.
   */
  public boolean stopContinuousConversion() {
    // Write the default config register to the ADC
    // Once we write, the ADC will do a single conversion and
    // enter power-off mode.
    int config = 0x8583; // Page 18 datasheet.
    byte[] bytes = {(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};
    this.command(ADS1015_REG_POINTER_CONFIG, bytes);
    return true;
  }

  /**
   * Returns the last ADC conversion result in mV
   */
  public float getLastConversionResults() {
    // Read the conversion results
    return readConversionResult();
  }

  public void startSingleEndedComparator(Channels channel, int thresholdHigh, int thresholdLow) {
    startSingleEndedComparator(channel, thresholdHigh, thresholdLow, pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  public void startSingleEndedComparator(Channels channel, int thresholdHigh, int thresholdLow, int pga, int sps) {
    startSingleEndedComparator(channel, thresholdHigh, thresholdLow, pga, sps, true, true, false, 1);
  }

  /**
   * Starts the comparator mode on the specified channel, see datasheet pg. 15.
   * In traditional mode it alerts (ALERT pin will go low)  when voltage exceeds
   * thresholdHigh until it falls below thresholdLow (both given in mV).
   * In window mode (traditionalMode=False) it alerts when voltage doesn't lie
   * between both thresholds.
   * In latching mode the alert will continue until the conversion value is read.
   * numReadings controls how many readings are necessary to trigger an alert: 1, 2 or 4.
   * Use getLastConversionResults() to read the current value  (which may differ
   * from the one that triggered the alert) and clear the alert pin in latching mode.
   * This function starts the continuous conversion mode.  The sps controls
   * the sample rate and the pga the gain, see datasheet page 13.
   */
  public void startSingleEndedComparator(Channels channel,
                                         int thresholdHigh,
                                         int thresholdLow,
                                         int pga,
                                         int sps,
                                         boolean activeLow,
                                         boolean traditionalMode,
                                         boolean latching,
                                         int numReadings) {
    // Continuous mode
    int config = ADS1015_REG_CONFIG_MODE_CONTIN;

    if (!activeLow) {
      config |= ADS1015_REG_CONFIG_CPOL_ACTVHI;
    } else {
      config |= ADS1015_REG_CONFIG_CPOL_ACTVLOW;
    }
    if (!traditionalMode) {
      config |= ADS1015_REG_CONFIG_CMODE_WINDOW;
    } else {
      config |= ADS1015_REG_CONFIG_CMODE_TRAD;
    }
    if (latching) {
      config |= ADS1015_REG_CONFIG_CLAT_LATCH;
    } else {
      config |= ADS1015_REG_CONFIG_CLAT_NONLAT;
    }
    if (numReadings == 4) {
      config |= ADS1015_REG_CONFIG_CQUE_4CONV;
    } else if (numReadings == 2) {
      config |= ADS1015_REG_CONFIG_CQUE_2CONV;
    } else {
      config |= ADS1015_REG_CONFIG_CQUE_1CONV;
    }
    // Set sample per seconds, defaults to 250sps
    // If sps is in the dictionary (defined in init()) it returns the value of the constant
    // othewise it returns the value for 250sps. This saves a lot of if/elif/else code!
    if (this.adcType == ICType.IC_ADS1015) {
      config |= spsADS1015.setDefault(sps, ADS1015_REG_CONFIG_DR_1600SPS);
    } else {
      config |= spsADS1115.setDefault(sps, ADS1115_REG_CONFIG_DR_250SPS);
    }
    // Set PGA/voltage range, defaults to +-6.144V
    config |= pgaADS1x15.setDefault(pga, ADS1015_REG_CONFIG_PGA_6_144V);
    this.pga = pga;

    // Set the channel to be converted
    if (channel == Channels.CHANNEL_3) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_3;
    } else if (channel == Channels.CHANNEL_2) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_2;
    } else if (channel == Channels.CHANNEL_1) {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_1;
    } else {
      config |= ADS1015_REG_CONFIG_MUX_SINGLE_0;
    }
    // Set 'start single-conversion' bit to begin conversions
    config |= ADS1015_REG_CONFIG_OS_SINGLE;

    // Write threshold high and low registers to the ADC
    // V_digital = (2^(n-1)-1)/pga*V_analog
    int thresholdHighWORD = 0;
    if (this.adcType == ICType.IC_ADS1015) {
      thresholdHighWORD = (int) (thresholdHigh * (2048.0 / pga));
    } else {
      thresholdHighWORD = (int) (thresholdHigh * (32767.0 / pga));
    }
    byte[] bytes = {(byte) ((thresholdHighWORD >> 8) & 0xFF), (byte) (thresholdHighWORD & 0xFF)};
    this.command(ADS1015_REG_POINTER_HITHRESH, bytes);

    int thresholdLowWORD = 0;
    if (this.adcType == ICType.IC_ADS1015) {
      thresholdLowWORD = (int) (thresholdLow * (2048.0 / pga));
    } else {
      thresholdLowWORD = (int) (thresholdLow * (32767.0 / pga));
    }
    bytes = new byte[]{(byte) ((thresholdLowWORD >> 8) & 0xFF), (byte) (thresholdLowWORD & 0xFF)};
    this.command(ADS1015_REG_POINTER_LOWTHRESH, bytes);

    // Write config register to the ADC
    // Once we write the ADC will convert continously and alert when things happen,
    // we can read the converted values using getLastConversionResult
    bytes = new byte[]{(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};
    this.command(ADS1015_REG_POINTER_CONFIG, bytes);
  }

  public void startDifferentialComparator(Channels chP, Channels chN, int thresholdHigh, int thresholdLow) {
    startDifferentialComparator(chP, chN, thresholdHigh, thresholdLow, pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(), spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning());
  }

  public void startDifferentialComparator(Channels chP, Channels chN, int thresholdHigh, int thresholdLow, int pga, int sps) {
    startDifferentialComparator(chP, chN, thresholdHigh, thresholdLow, pga, sps, true, true, false, 1);
  }

  /**
   * Starts the comparator mode on the specified channel, see datasheet pg. 15.
   * In traditional mode it alerts (ALERT pin will go low)  when voltage exceeds
   * thresholdHigh until it falls below thresholdLow (both given in mV).
   * In window mode (traditionalMode=False) it alerts when voltage doesn't lie
   * between both thresholds.
   * In latching mode the alert will continue until the conversion value is read.
   * numReadings controls how many readings are necessary to trigger an alert: 1, 2 or 4.
   * Use getLastConversionResults() to read the current value  (which may differ
   * from the one that triggered the alert) and clear the alert pin in latching mode.
   * This function starts the continuous conversion mode.  The sps controls
   * the sample rate and the pga the gain, see datasheet page 13.
   */
  public void startDifferentialComparator(Channels chP,
                                          Channels chN,
                                          int thresholdHigh,
                                          int thresholdLow,
                                          int pga,
                                          int sps,
                                          boolean activeLow,
                                          boolean traditionalMode,
                                          boolean latching,
                                          int numReadings) {

    // Continuous mode
    int config = ADS1015_REG_CONFIG_MODE_CONTIN;

    if (!activeLow) {
      config |= ADS1015_REG_CONFIG_CPOL_ACTVHI;
    } else {
      config |= ADS1015_REG_CONFIG_CPOL_ACTVLOW;
    }
    if (!traditionalMode) {
      config |= ADS1015_REG_CONFIG_CMODE_WINDOW;
    } else {
      config |= ADS1015_REG_CONFIG_CMODE_TRAD;
    }
    if (latching) {
      config |= ADS1015_REG_CONFIG_CLAT_LATCH;
    } else {
      config |= ADS1015_REG_CONFIG_CLAT_NONLAT;
    }
    if (numReadings == 4) {
      config |= ADS1015_REG_CONFIG_CQUE_4CONV;
    } else if (numReadings == 2) {
      config |= ADS1015_REG_CONFIG_CQUE_2CONV;
    } else {
      config |= ADS1015_REG_CONFIG_CQUE_1CONV;
    }
    // Set sample per seconds, defaults to 250sps
    // If sps is in the dictionary (defined in init()) it returns the value of the constant
    // othewise it returns the value for 250sps. This saves a lot of if/elif/else code!
    if (this.adcType == ICType.IC_ADS1015) {
      config |= spsADS1015.setDefault(sps, ADS1015_REG_CONFIG_DR_1600SPS);
    } else {
      config |= spsADS1115.setDefault(sps, ADS1115_REG_CONFIG_DR_250SPS);
    }
    // Set PGA/voltage range, defaults to +-6.144V
    config |= pgaADS1x15.setDefault(pga, ADS1015_REG_CONFIG_PGA_6_144V);
    this.pga = pga;

    // Set channels
    if ((chP == Channels.CHANNEL_0) & (chN == Channels.CHANNEL_1)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_0_1;
    } else if ((chP == Channels.CHANNEL_0) & (chN == Channels.CHANNEL_3)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_0_3;
    } else if ((chP == Channels.CHANNEL_2) & (chN == Channels.CHANNEL_3)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_2_3;
    } else if ((chP == Channels.CHANNEL_1) & (chN == Channels.CHANNEL_3)) {
      config |= ADS1015_REG_CONFIG_MUX_DIFF_1_3;
    } else {
      if (verbose) {
        System.out.printf("ADS1x15: Invalid channels specified: %d, %d", chP, chN);
      }
      return;
    }
    // Set 'start single-conversion' bit to begin conversions
    config |= ADS1015_REG_CONFIG_OS_SINGLE;

    // Write threshold high and low registers to the ADC
    // V_digital = (2^(n-1)-1)/pga*V_analog
    int thresholdHighWORD = 0;
    if (this.adcType == ICType.IC_ADS1015) {
      thresholdHighWORD = (int) (thresholdHigh * (2048.0 / pga));
    } else {
      thresholdHighWORD = (int) (thresholdHigh * (32767.0 / pga));
    }
    byte[] bytes = {(byte) ((thresholdHighWORD >> 8) & 0xFF), (byte) (thresholdHighWORD & 0xFF)};
    this.command(ADS1015_REG_POINTER_HITHRESH, bytes);

    int thresholdLowWORD = 0;
    if (this.adcType == ICType.IC_ADS1015) {
      thresholdLowWORD = (int) (thresholdLow * (2048.0 / pga));
    } else {
      thresholdLowWORD = (int) (thresholdLow * (32767.0 / pga));
    }
    bytes = new byte[]{(byte) ((thresholdLowWORD >> 8) & 0xFF), (byte) (thresholdLowWORD & 0xFF)};
    this.command(ADS1015_REG_POINTER_LOWTHRESH, bytes);

    // Write config register to the ADC
    // Once we write the ADC will convert continously and alert when things happen,
    // we can read the converted values using getLastConversionResult
    bytes = new byte[]{(byte) ((config >> 8) & 0xFF), (byte) (config & 0xFF)};
    this.command(ADS1015_REG_POINTER_CONFIG, bytes);
  }

  private float readConversionResult() {
    super.beginTransmission(this.address);
    super.write((byte)ADS1015_REG_POINTER_CONVERT);
    byte[] result = super.read(2);
    super.endTransmission();
    float returnVal = 0;
    if (this.adcType == ICType.IC_ADS1015) {
      // Shift right 4 bits for the 12-bit ADS1015 and convert to mV
      returnVal = (((result[0] << 8) | (result[1] & 0xFF)) >> 4);
      returnVal = (float) (returnVal * pga / 2048.0);
    } else {
      // Return a mV value for the ADS1115
      // (Take signed values into account as well)
      returnVal = (result[0] << 8) | (result[1]);
      if (returnVal > 0x7FFF) {
        returnVal = (returnVal - 0xFFFF);
        returnVal = (float) (returnVal * pga / 32768.0);
      } else {
        returnVal = ((result[0] << 8) | (result[1]));
        returnVal = (float) (returnVal * pga / 32768.0);
      }
    }
    return returnVal;
  }
}
