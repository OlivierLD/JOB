package examples.io.servo;

import jhard.io.SoftwareServo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DirectPWMServo {

	private static DirectPWMServo instance = null;
	private DirectPWMServo() {
	}

	public static synchronized DirectPWMServo getInstance() {
		if (instance == null) {
			instance = new DirectPWMServo();
		}
		return instance;
	}

	private static void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			// Absorb
		}
	}

	private static final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	public static String userInput(String prompt) {
		String retString = "";
		System.err.print(prompt);
		try {
			retString = stdin.readLine();
		} catch (Exception e) {
			System.out.println(e);
			try {
				userInput("<Oooch/>");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return retString;
	}

	public static void main(String... args) {
		int pinNum = 5; // Physical #29
		if (args.length > 0) {
			try {
				pinNum = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfe) {
				System.err.println("Oops!\n" + nfe.getMessage());
			}
		}
		System.out.println(String.format("Using pin #%d", pinNum));
		SoftwareServo ss = new SoftwareServo(getInstance());
		System.out.println("Let's go. Enter Q to quit");
		try {
			ss.attach(pinNum);

			System.out.println("Setting servo to 0");
			ss.write(0f);
			delay(1_000);

			boolean go = true;
			while (go) {
				String angleStr = userInput("Set angle [0..180] > ");
				if ("q".equalsIgnoreCase(angleStr)) {
					go = false;
				} else {
					try {
						float angle = Float.parseFloat(angleStr);
						ss.write(angle);
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}
				}
			}
			System.out.println("Parking the servo");
			ss.write(0f);
			delay(500);
			ss.detach();

		} finally {
			ss.close();
			System.out.println("Done!");
		}
	}
}
