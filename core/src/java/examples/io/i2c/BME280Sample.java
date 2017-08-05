package examples.io.i2c;

import jhard.devices.BME280;

public class BME280Sample {

  public static void main(String... args) {
    BME280 bme280 = new BME280();
		if ("true".equals(System.getProperty("bme280.verbose", "false")))
			System.out.println("BME280 ready...");

		bme280.close();
		if ("true".equals(System.getProperty("bme280.verbose", "false"))) {
			System.out.println("Done.");
		}
  }
}
