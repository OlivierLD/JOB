package utils;

public class MiscUtils {
	public static void delay(long howMuch) {
		try {
			Thread.sleep(howMuch);
		} catch (Exception absorbed) {
		}
	}
}
