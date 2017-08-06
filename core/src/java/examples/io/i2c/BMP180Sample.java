package examples.io.i2c;

import jhard.devices.BMP180;

public class BMP180Sample {

  public static void main(String... args) {
	  BMP180 bmp180 = new BMP180();
		if ("true".equals(System.getProperty("bmp180.verbose", "false")))
			System.out.println("BMP180 ready...");

		bmp180.close();
		if ("true".equals(System.getProperty("bmp180.verbose", "false"))) {
			System.out.println("Done.");
		}
  }
}
