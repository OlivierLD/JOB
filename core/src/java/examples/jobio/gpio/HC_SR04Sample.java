package examples.jobio.gpio;

import job.devices.HC_SR04;
import job.devices.STH10;
import job.io.GPIO;
import utils.MiscUtils;

public class HC_SR04Sample {

	private static int ECHO = 24,
										 TRIG = 23;

	private static double dist = 0d;

	public static void main(String... args) {
		HC_SR04 hc_sr04 = new HC_SR04(TRIG, ECHO);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			GPIO.releasePin(TRIG);
			GPIO.releasePin(ECHO);
		}));

		System.out.println("Hit [Ctrl + C] to quit.");
		while (true) {
			try {
				dist = hc_sr04.readDistance();
			} catch (Exception ex) {
				dist = 0;
				ex.printStackTrace();
			}
			System.out.println(String.format("Dist: %.02f cm", dist));
			MiscUtils.delay(500L);
		}
	}
}
