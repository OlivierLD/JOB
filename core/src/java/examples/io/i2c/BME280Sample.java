package examples.io.i2c;

import jhard.devices.BME280;

public class BME280Sample {

	public static void main(String... args) {
    BME280 bme280 = new BME280();
		if ("true".equals(System.getProperty("bme280.verbose", "false")))
			System.out.println("BME280 ready...");

		try {
			BME280.BME280Data data = bme280.getAllData(); // Sea level.
			System.out.println(String.format("Press: %.02f hPa", (data.getPress() / 100)));
			System.out.println(String.format("Alt  : %.02f m", data.getAlt()));
			System.out.println(String.format("Temp : %.02f \272C", data.getTemp()));
			System.out.println(String.format("Hum  : %.02f %%", data.getHum()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	  bme280.close();
		if ("true".equals(System.getProperty("bme280.verbose", "false"))) {
			System.out.println("Done.");
		}
  }
}
