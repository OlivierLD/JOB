package utils;

public class MiscUtils {

	public static void delay(long howMuch) {
		try {
			Thread.sleep(howMuch);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	public static void delay(long ms, int nano) {
		try {
			Thread.sleep(ms, nano);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

}
