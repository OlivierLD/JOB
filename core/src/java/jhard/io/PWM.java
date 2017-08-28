package jhard.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *  Generic Pulse Width Modulation (PWM)
 */
public class PWM {

  int channel;
  String chip;

  private final static int ENOENT =  -2;
  private final static int EINVAL = -22;
  private final static int EBUSY  = -16;

  /**
   *  Opens a PWM channel
   *  @param channel PWM channel
   *  @see #list
   */
  public PWM(String channel) {
    JHardNativeInterface.loadLibrary();

    int pos = channel.indexOf("/pwm");
    if (pos == -1) {
      throw new IllegalArgumentException("Unsupported channel");
    }
    this.chip = channel.substring(0, pos);
    this.channel = Integer.parseInt(channel.substring(pos+4));

    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    // export channel through sysfs
    String fName = "/sys/class/pwm/" + this.chip + "/export";
    int ret = JHardNativeInterface.writeFile(fName, Integer.toString(this.channel));
    if (ret < 0) {
      if (ret == ENOENT) {
        System.err.println("Make sure your kernel is compiled with PWM_SYSFS enabled and you have the necessary PWM driver for your platform");
      }
      if (ret == EINVAL) {
        System.err.println("PWM channel " + channel + " does not seem to be available on your platform");
      }
      if (ret != EBUSY) {   // Returned when the pin is already exported
        throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
      }
    }

    // delay to give udev a chance to change the file permissions behind our back
    // there should really be a cleaner way for this
    try {
      Thread.sleep(500L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   *  Disables the PWM output
   */
  public void clear() {
    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    String fName = String.format("/sys/class/pwm/%s/pwm%d/enable", this.chip, this.channel);
    int ret = JHardNativeInterface.writeFile(fName, "0");
    if (ret < 0) {
      throw new RuntimeException(JHardNativeInterface.getError(ret));
    }
  }

  /**
   *  Gives ownership of a channel back to the operating system
   */
  public void close() {
    if (JHardNativeInterface.isSimulated()) {
      return;
    }

    // XXX: implicit clear()?
    // XXX: also check GPIO

    String fName = "/sys/class/pwm/" + this.chip + "/unexport";
    int ret = JHardNativeInterface.writeFile(fName, Integer.toString(this.channel));
    if (ret < 0) {
      if (ret == ENOENT) {
        System.err.println("Make sure your kernel is compiled with PWM_SYSFS enabled and you have the necessary PWM driver for your platform");
      }
      // XXX: check
      // EINVAL is also returned when trying to unexport pins that weren't exported to begin with
      throw new RuntimeException(JHardNativeInterface.getError(ret));
    }
  }


  /**
   *  Lists all available PWM channels
   *  @return Device list
   */
  public static String[] list() {
    if (JHardNativeInterface.isSimulated()) {
      return new String[]{ "pwmchip0/pwm0", "pwmchip0/pwm1" };
    }

    List<String> devs = new ArrayList<>();
    File dir = new File("/sys/class/pwm");
    File[] chips = dir.listFiles();
    if (chips != null) {
      for (File chip : chips) {
        // get the number of supported channels
        try {
          Path path = Paths.get("/sys/class/pwm/" + chip.getName() + "/npwm");
          String tmp = new String(Files.readAllBytes(path));
          int npwm = Integer.parseInt(tmp.trim());
          for (int i=0; i < npwm; i++) {
            devs.add(chip.getName() + "/pwm" + i);
          }
        } catch (Exception e) {
          // Absorbed
        }
      }
    }
    // listFiles() does not guarantee ordering
    String[] tmp = devs.toArray(new String[devs.size()]);
    Arrays.sort(tmp);
    return tmp;
  }

  /**
   *  Enables the PWM output
   *  @param period cycle period in Hz
   *  @param duty duty cycle, 0.0 (always off) to 1.0 (always on)
   */
  public void set(int period, float duty) {
    if (JHardNativeInterface.isSimulated()) {
      return;
    }
    // set period
    String fName = String.format("/sys/class/pwm/%s/pwm%d/period", this.chip, this.channel);
    // convert to nanoseconds
    int ret = JHardNativeInterface.writeFile(fName, String.format("%d", (int)(1_000_000_000 / period)));
    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
    }

    // set duty cycle
    fName = fName = String.format("/sys/class/pwm/%s/pwm%d/duty_cycle", this.chip, this.channel);
    if (duty < 0.0 || 1.0 < duty) {
      System.err.println("Duty cycle must be between 0.0 and 1.0.");
      throw new IllegalArgumentException("Illegal argument");
    }
    // convert to nanoseconds
    ret = JHardNativeInterface.writeFile(fName, String.format("%d", (int)((1_000_000_000 * duty) / period)));
    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
    }

    // enable output
    fName = String.format("/sys/class/pwm/%s/pwm%d/enable", this.chip, this.channel);
    ret = JHardNativeInterface.writeFile(fName, "1");
    if (ret < 0) {
      throw new RuntimeException(fName + ": " + JHardNativeInterface.getError(ret));
    }
  }


  /**
   *  Enables the PWM output with a preset period of 1 kHz
   *  @param duty duty cycle, 0.0 (always off) to 1.0 (always on)
   */
  public void set(float duty) {
    set(1_000, duty);
  }
}
