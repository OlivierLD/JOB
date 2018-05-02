package examples.io.i2c;

import jhard.devices.ADS1x15;

public class ADS1015Sample {

	private final static ADS1x15.ICType ADC_TYPE = ADS1x15.ICType.IC_ADS1015;
	private static int gain = 6_144;
	private static int sps  =   250;

	private static boolean go = true;
	private static void go(boolean b) {
		go = b;
	}
	private static boolean go() {
		return go;
	}

	public static void main(String... args) {
		ADS1x15 ads1015 = new ADS1x15(ADC_TYPE);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			go(false);
		}));

		while (go()) {
			float value = ads1015.readADCSingleEnded(ADS1x15.Channels.CHANNEL_1, gain, sps);
			System.out.println(String.format("Value: %f", value));
		}
		ads1015.close();
	}
}
