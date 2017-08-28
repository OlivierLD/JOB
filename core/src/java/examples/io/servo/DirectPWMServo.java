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
		SoftwareServo ss = new SoftwareServo(getInstance());
		try {
			ss.attach(27);
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
