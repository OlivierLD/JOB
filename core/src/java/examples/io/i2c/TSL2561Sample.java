package examples.io.i2c;

import jhard.devices.TSL2561;
import utils.MiscUtils;

public class TSL2561Sample {
	private static boolean go = true;
	public static void main(String... args) {
		TSL2561 tsl2561 = new TSL2561();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			go = false;
			tsl2561.close();
			MiscUtils.delay(1_000L);
		}));

		while (go) {
			double lux = tsl2561.readLux();
			System.out.println(String.format("Light: %.02f lux", lux));
		}
		System.out.println("Done.");
	}
}
