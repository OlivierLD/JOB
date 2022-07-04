package examples.jobio.i2c;

import job.devices.VL53L0X;
import utils.MiscUtils;

public class VL53L0XSample {
	private static boolean go = true;
	public static void main(String... args) {
		VL53L0X vl53l0x = new VL53L0X();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			go = false;
			vl53l0x.close();
			MiscUtils.delay(1_000L);
		}, "Interrupter"));

		int previous = -1;
		while (go) {
			int range = vl53l0x.range();
			if (range != previous) {
				System.out.printf("Dist: %d mm\n", range);
			}
			previous = range;
		}
		System.out.println("Done.");
	}
}
