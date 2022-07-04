package examples.jobio.gpio;

import job.devices.STH10;
import job.io.GPIO;
import utils.MiscUtils;

public class STH10Sample {

	private final static int DATA = 18, CLOCK = 23;
	private static boolean keepLooping = true;

	public static void main(String... args) {
		double temp, hum;

		STH10 sth10 = new STH10(DATA, CLOCK);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			GPIO.releasePin(DATA);
			GPIO.releasePin(CLOCK);
			keepLooping = false;
		}, "Interrupter"));

		while (keepLooping) {
			try {
				temp = sth10.readTemperature();
				hum = sth10.readHumidity(temp);
			} catch (Exception ex) {
				temp = 20d;
				hum = 50d;
				ex.printStackTrace();
			}
			System.out.printf("Temp %.02f\272C, Hum: %.02f%%\n", temp, hum);
			MiscUtils.delay(1_000L);
		}
		System.out.println("Bye!");
	}
}
