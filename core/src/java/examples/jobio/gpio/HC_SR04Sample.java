package examples.jobio.gpio;

import job.devices.HC_SR04;
import job.io.GPIO;
import utils.MiscUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class HC_SR04Sample {

    private final static int ECHO = 24, TRIG = 23;

    public static void main(String... args) {
        HC_SR04 hc_sr04 = new HC_SR04(TRIG, ECHO);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            GPIO.releasePin(TRIG);
            GPIO.releasePin(ECHO);
        }));

        System.out.println("Hit [Ctrl + C] to quit.");
		double dist;
		AtomicBoolean keepLooping = new AtomicBoolean(true);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> keepLooping.set(false), "Interrupter"));
		while (keepLooping.get()) {
			try {
				dist = hc_sr04.readDistance();
			} catch (Exception ex) {
				dist = 0;
				ex.printStackTrace();
			}
			if (!"true".equals(System.getProperty("hc_sr04.verbose"))) { // Otherwise, displayed already
				System.out.printf("Dist: %.02f cm\n", dist);
			}
			MiscUtils.delay(500L);
		}
    }
}
