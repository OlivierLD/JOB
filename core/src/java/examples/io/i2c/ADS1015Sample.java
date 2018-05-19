package examples.io.i2c;

import jhard.devices.ADS1x15;

public class ADS1015Sample {

	private final static ADS1x15.ICType ADC_TYPE = ADS1x15.ICType.IC_ADS1015;
	private static int gain = ADS1x15.pgaADS1x15.ADS1015_REG_CONFIG_PGA_6_144V.meaning(); // +/- 6.144 V
	private static int sps  = ADS1x15.spsADS1015.ADS1015_REG_CONFIG_DR_250SPS.meaning();  // 250 Samples per Second

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
			System.out.println(String.format("Value: %f, %.03f V", value, (value / 1_000)));
		}
		ads1015.close();
	}
}
