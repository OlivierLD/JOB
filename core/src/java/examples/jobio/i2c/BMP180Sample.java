package examples.jobio.i2c;

import job.devices.BMP180;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BMP180Sample {

    public static void main(String... args) {

        final NumberFormat NF = new DecimalFormat("##00.00");

        BMP180 bmp180 = new BMP180();
        if ("true".equals(System.getProperty("bmp180.verbose", "false"))) {
			System.out.println("BMP180 ready...");
		}

        float press = 0;
        float temp = 0;
        double alt = 0;

        try {
            press = bmp180.readPressure();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        bmp180.setStandardSeaLevelPressure((int) press); // As we ARE at the sea level (in San Francisco).
        try {
            alt = bmp180.readAltitude();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        try {
            temp = bmp180.readTemperature();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("Temperature: " + NF.format(temp) + " C");
        System.out.println("Pressure   : " + NF.format(press / 100) + " hPa");
        System.out.println("Altitude   : " + NF.format(alt) + " m");

        bmp180.close();
        if ("true".equals(System.getProperty("bmp180.verbose", "false"))) {
            System.out.println("Done.");
        }
    }
}
