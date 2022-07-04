package examples.jobio.i2c;

import job.devices.BME280;

public class BME280Sample {

	// Read once
    public static void main(String... args) {
        BME280 bme280 = new BME280();
        if ("true".equals(System.getProperty("bme280.verbose", "false"))) {
			System.out.println("BME280 ready...");
		}
        try {
            BME280.BME280Data data = bme280.getAllData(); // Sea level.
            System.out.printf("Press: %.02f hPa\n", (data.getPress() / 100));
            System.out.printf("Alt  : %.02f m\n", data.getAlt());
            System.out.printf("Temp : %.02f \272C\n", data.getTemp());
            System.out.printf("Hum  : %.02f %%\n", data.getHum());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        bme280.close();
        if ("true".equals(System.getProperty("bme280.verbose", "false"))) {
            System.out.println("Done.");
        }
    }
}
