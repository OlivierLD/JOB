package utils;

public class MiscUtils {

	/**
	 * @param howMuch in ms.
	 */
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

	/**
	 *
	 * @param sec in seconds
	 */
	public static void delay(float sec) {
		delay(Math.round(sec * 1_000));
	}
}
