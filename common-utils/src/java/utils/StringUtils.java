package utils;

public class StringUtils {
	/**
	 * Right pad, with blanks
	 *
	 * @param s Original String
	 * @param len Final String length
	 * @return the added String
	 */
	public static String rpad(String s, int len) {
		return rpad(s, len, " ");
	}

	public static String rpad(String s, int len, String pad) {
		String str = s;
		while (str.length() < len) {
			str += pad;
		}
		return str;
	}

	/**
	 * Left pad, with blanks
	 *
	 * @param s Original String
	 * @param len Final String length
	 * @return the added String
	 */
	public static String lpad(String s, int len) {
		return lpad(s, len, " ");
	}

	public static String lpad(String s, int len, String pad) {
		String str = s;
		while (str.length() < len) {
			str = pad + str;
		}
		return str;
	}
}
