package examples.io.i2c;

import jhard.devices.BME280;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BME280Sample {

	private final static NumberFormat NF = new DecimalFormat("##00.00");

	public static void main(String... args) {
    BME280 bme280 = new BME280();
		if ("true".equals(System.getProperty("bme280.verbose", "false")))
			System.out.println("BME280 ready...");

		float press = 0f, alt = 0f, temp = 0f, hum = 0f;
		// WARNING!! The read order is important!
		// temperature, pressure (analog to altitude), humidity.
		try {
			temp = bme280.readTemperature();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	  try {
		  press = bme280.readPressure();
	  } catch (Exception ex) {
		  System.err.println(ex.getMessage());
		  ex.printStackTrace();
	  }
		bme280.setStandardSeaLevelPressure((int) press); // As we ARE at the sea level (in San Francisco).
	  try {
		  alt = (float)bme280.readAltitude();
	  } catch (Exception ex) {
		  System.err.println(ex.getMessage());
		  ex.printStackTrace();
	  }
		try {
			hum = bme280.readHumidity();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}

	  System.out.println("Temperature: " + NF.format(temp) + " C");
	  System.out.println("Pressure   : " + NF.format(press / 100) + " hPa");
	  System.out.println("Altitude   : " + NF.format(alt) + " m");
		System.out.println("Humidity   : " + NF.format(hum) + " %");

	  bme280.close();
		if ("true".equals(System.getProperty("bme280.verbose", "false"))) {
			System.out.println("Done.");
		}
  }
}
