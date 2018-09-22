package job.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


/**
 *  Generic LED interface (internal LED)
 */
public class LED {

  protected String dev;
  protected int maxBrightness;
  protected int prevBrightness;
  protected String prevTrigger;

  private final static int EACCES = -13;
  /**
   *  Opens a LED device
   *  @param dev device name
   *  @see #list
   */
  public LED(String dev) {
    JHardNativeInterface.loadLibrary();
    this.dev = dev;

    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    // read maximum brightness
    try {
      Path path = Paths.get("/sys/class/leds/" + dev + "/max_brightness");
      String tmp = new String(Files.readAllBytes(path));
      maxBrightness = Integer.parseInt(tmp.trim());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw new RuntimeException("Unable to read maximum brightness");
    }

    // read current trigger setting to be able to restore it later
    try {
      Path path = Paths.get("/sys/class/leds/" + dev + "/trigger");
      String tmp = new String(Files.readAllBytes(path));
      int start = tmp.indexOf('[');
      int end = tmp.indexOf(']', start);
      if (start != -1 && end != -1) {
        prevTrigger = tmp.substring(start+1, end);
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw new RuntimeException("Unable to read trigger setting");
    }

    // read current brightness to be able to restore it later
    try {
      Path path = Paths.get("/sys/class/leds/" + dev + "/brightness");
      String tmp = new String(Files.readAllBytes(path));
      prevBrightness = Integer.parseInt(tmp.trim());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw new RuntimeException("Unable to read current brightness");
    }
    // disable trigger
    String fName = "/sys/class/leds/" + dev + "/trigger";
    int ret = JHardNativeInterface.writeFile(fName, "none");
    if (ret < 0) {
      if (ret == EACCES) {
        System.err.println("You might need to install a custom udev rule to allow regular users to modify /sys/class/leds/*.");
      }
      throw new RuntimeException(JHardNativeInterface.getError(ret));
    }
  }

  public boolean isSimulated() {
    return JHardNativeInterface.isSimulated();
  }

  /**
   *  Sets the brightness
   *  @param bright 0.0 (off) to 1.0 (maximum)
   */
  public void brightness(float bright) {
    if (bright < 0.0 || 1.0 < bright) {
      System.err.println("Brightness must be between 0.0 and 1.0.");
      throw new IllegalArgumentException("Illegal argument");
    }
    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    String fName = "/sys/class/leds/" + dev + "/brightness";
    int ret = JHardNativeInterface.writeFile(fName, Integer.toString((int)(bright * maxBrightness)));
    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
    }
  }

  /**
   *  Restores the previous state
   */
  public void close() {
    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    // restore previous settings
    String fName = "/sys/class/leds/" + dev + "/brightness";
    int ret = JHardNativeInterface.writeFile(fName, Integer.toString(prevBrightness));
    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
    }

    fName = "/sys/class/leds/" + dev + "/trigger";
    ret = JHardNativeInterface.writeFile(fName, prevTrigger);
    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
    }
  }

  /**
   *  Lists all available LED devices
   *  @return String array
   */
  public static String[] list() {
    if (JHardNativeInterface.isSimulated()) {
      // as on the Raspberry Pi
      return new String[]{ "led0", "led1" };
    }

    List<String> devs = new ArrayList<>();
    File dir = new File("/sys/class/leds");
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        devs.add(file.getName());
      }
    }
    // listFiles() does not guarantee ordering
    String[] tmp = devs.toArray(new String[devs.size()]);
    Arrays.sort(tmp);
    return tmp;
  }
}
