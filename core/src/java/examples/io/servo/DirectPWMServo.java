package examples.io.servo;

import jhard.io.SoftwareServo;

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
	public static void main(String... args) {
		int pinNum = 5;
		if (args.length > 0) {
			try {
				pinNum = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfe) {
				System.err.println("Oops!\n" + nfe.getMessage());
			}
		}
		System.out.println(String.format("Using pin #%d", pinNum));

		SoftwareServo ss = new SoftwareServo(getInstance());
		try {
			ss.attach(pinNum);
			ss.write(90f);
			delay(1_000);
			ss.write(45f);
			delay(1_000);
			ss.write(135f);
			delay(1_000);
			ss.write(0f);
			ss.detach();

		} finally {
			ss.close();
		}
	}
}
