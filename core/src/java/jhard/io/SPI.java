package jhard.io;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *  Generic SPI Communication
 */
public class SPI {

	public final static int DEFAULT_SPEED = 500_000;

  public enum SPIMode {
    MODE0(0), // CPOL=0, CPHA=0, most common
    MODE1(1), // CPOL=0, CPHA=1
    MODE2(2), // CPOL=1, CPHA=0
    MODE3(3); // CPOL=1, CPHA=1

    private int intVal;
    SPIMode(int intVal) {
      this.intVal = intVal;
    }
    public int intVal() {
      return this.intVal;
    }
  };

	/**
	 *  most significant bit first, most common
	 */
	private static final int MSB_FIRST = 0;
	/**
	 *  least significant bit first
	 */
	private static final int LSB_FIRST = 1;

  public enum Endianness {
    LITTLE_ENDIAN(MSB_FIRST),
    BIG_ENDIAN(LSB_FIRST);

	  private int order;
    Endianness(int order) { this.order = order; }
    public int order() { return this.order; }
  }

  protected Endianness endianness = Endianness.LITTLE_ENDIAN; // MSBFIRST;
  protected String dev;
  protected int handle;
  protected int maxSpeed = DEFAULT_SPEED;
  protected SPIMode mode = SPIMode.MODE0;
  protected static Map<String, String> settings = new HashMap<>();

  /**
   *  Opens an SPI interface as master
   *  @param dev device name
   *  @see #list
   */
  public SPI(String dev) {
    JHardNativeInterface.loadLibrary();
    this.dev = dev;

    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    this.handle = JHardNativeInterface.openDevice("/dev/" + dev);
    if (this.handle < 0) {
      throw new RuntimeException(JHardNativeInterface.getError(this.handle));
    }
  }

  public boolean isSimulated() {
    return JHardNativeInterface.isSimulated();
  }

  /**
   *  Closes the SPI interface
   */
  public void close() {
    if (JHardNativeInterface.isSimulated()) {
      return;
    }
    JHardNativeInterface.closeDevice(this.handle);
    this.handle = 0;
  }


  protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    }
  }


  /**
   *  Lists all available SPI interfaces
   *  @return Device list
   */
  public static String[] list() {
    if (JHardNativeInterface.isSimulated()) {
      // as on the Raspberry Pi
      return new String[]{ "spidev0.0", "spidev0.1" };
    }

    List<String> devs = new ArrayList<>();
    File dir = new File("/dev");
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().startsWith("spidev")) {
          devs.add(file.getName());
        }
      }
    }
    // listFiles() does not guarantee ordering
    String[] tmp = devs.toArray(new String[devs.size()]);
    Arrays.sort(tmp);
    return tmp;
  }

  /**
   *  Configures the SPI interface
   *  @param maxSpeed maximum transmission rate in Hz, 500,000 (500 kHz) is a reasonable default
   *  @param endianness whether data is send with the first- or least-significant bit first (SPI.MSBFIRST or SPI.LSBFIRST, the former is more common)
   *  @param mode <a href="https://en.wikipedia.org/wiki/Serial_Peripheral_Interface_Bus#Clock_polarity_and_phase">SPI.MODE0 to SPI.MODE3</a>
   */
  public void settings(int maxSpeed, Endianness endianness, SPIMode mode) {
    this.maxSpeed = maxSpeed;
    this.endianness = endianness;
    this.mode = mode;
  }

  /**
   *  Transfers data over the SPI bus
   *  @param out bytes to send
   *  @return bytes read in (array is the same length as out)
   */
  public byte[] transfer(byte[] out) {
    if (JHardNativeInterface.isSimulated()) {
      return new byte[out.length];
    }

    // track the current setting per device across multiple instances
    String curSettings = this.maxSpeed + "-" + this.endianness + "-" + this.mode;
    if (!curSettings.equals(settings.get(this.dev))) {
      int ret = JHardNativeInterface.setSpiSettings(this.handle, this.maxSpeed, this.endianness.order(), this.mode.intVal());
      if (ret < 0) {
        System.err.println(JHardNativeInterface.getError(this.handle));
        throw new RuntimeException("Error updating device configuration");
      }
      settings.put(this.dev, curSettings);
    }

    byte[] in = new byte[out.length];
    int transferred = JHardNativeInterface.transferSpi(this.handle, out, in);
    if (transferred < 0) {
      throw new RuntimeException(JHardNativeInterface.getError(transferred));
    } else if (transferred < out.length) {
      throw new RuntimeException("Fewer bytes transferred than requested: " + transferred);
    }
    return in;
  }

  /**
   *  Transfers data over the SPI bus
   *  @param out string to send
   *  @return bytes read in (array is the same length as out)
   */
  public byte[] transfer(String out) {
    return transfer(out.getBytes());
  }

  /**
   *  Transfers data over the SPI bus
   *  @param out single byte to send, e.g. numeric literal (0 to 255, or -128 to 127)
   *  @return bytes read in (array is the same length as out)
   */
  public byte[] transfer(int out) {
    if (out < -128 || out > 255) {
      System.err.println("The transfer function can only operate on a single byte at a time. Call it with a value from 0 to 255, or -128 to 127.");
      throw new RuntimeException("Argument does not fit into a single byte");
    }
    byte[] tmp = new byte[1];
    tmp[0] = (byte)out;
    return transfer(tmp);
  }

  /**
   *  Transfers data over the SPI bus
   *  @param out single byte to send
   *  @return bytes read in (array is the same length as out)
   */
  public byte[] transfer(byte out) {
    // cast to (unsigned) int
    return transfer(out & 0xff);
  }
}
