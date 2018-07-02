package examples.io.gpio;

import jhard.devices.STH10;
import jhard.io.GPIO;
import utils.MiscUtils;

public class STH10Sample {

	private static int DATA = 18,
										 CLOCK = 23;

	private static double temp = 0d, hum = 0d;

	public static void main(String... args) {
		STH10 sth10 = new STH10(DATA, CLOCK);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			GPIO.releasePin(DATA);
			GPIO.releasePin(CLOCK);
		}));

		while (true) {
			try {
				temp = sth10.readTemperature();
				hum = sth10.readHumidity(temp);
			} catch (Exception ex) {
				temp = 20d;
				hum = 50d;
				ex.printStackTrace();
			}
			System.out.println(String.format("Temp %.02f\272C, Hum: %.02f%%", temp, hum));
			MiscUtils.delay(1_000L);
		}
	}
}
