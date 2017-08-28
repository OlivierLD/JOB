package jhard.io;

public class JHardNativeInterface {

  protected static boolean loaded = false;
  protected static boolean alwaysSimulate = false;

  public static void loadLibrary() {
    if (!loaded) {
      if (isSimulated()) {
        System.err.println(String.format(
            "The JavaHard I/O library is not supported on this platform [%s].\nInstead of values from actual hardware ports, your code will only receive stand-in values that allow you to test the remainder of its functionality.",
            System.getProperty("os.name")));
      } else {
        System.loadLibrary("javahard-io");
      }
      loaded = true;
    }
  }

  public static void alwaysSimulate() {
    alwaysSimulate = true;
  }

  public static boolean isSimulated() {
    return alwaysSimulate ||
           !"Linux".equals(System.getProperty("os.name"));
  }

  public static native int openDevice(String fName); // fn: File Name
  public static native String getError(int errno);
  public static native int closeDevice(int handle);

  // the following two functions were done in native code to get access to the
  // specific error number (errno) that might occur
  public static native int readFile(String fName, byte[] in);
  public static native int writeFile(String fName, byte[] out);
  public static int writeFile(String fName, String out) {
    return writeFile(fName, out.getBytes());
  }

  /* GPIO */
  public static native int pollDevice(String fName, int timeout);
  /* I2C */
  public static native int transferI2c(int handle, int slave, byte[] out, byte[] in);
  /* SoftwareServo */
  public static native long servoStartThread(int gpio, int pulse, int period);
  public static native int servoUpdateThread(long handle, int pulse, int period);
  public static native int servoStopThread(long handle);
  /* SPI */
  public static native int setSpiSettings(int handle, int maxSpeed, int dataOrder, int mode);
  public static native int transferSpi(int handle, byte[] out, byte[] in);
}
