package examples.io.i2c;

import jhard.devices.PCA9685;
import static utils.MiscUtils.delay;

public class PCA9685Sample {
	/*
	 * Servo       | Standard |   Continuous
	 * ------------+----------+-------------------
	 * 1.5ms pulse |   0 deg  |     Stop
	 * 2ms pulse   |  90 deg  | FullSpeed forward
	 * 1ms pulse   | -90 deg  | FullSpeed backward
	 * ------------+----------+-------------------
	 */
	public static void main(String... args)  {
		int freq = 60;
		if (args.length > 0) {
			freq = Integer.parseInt(args[0]);
		}
		PCA9685 servoBoard = new PCA9685();
		servoBoard.setPWMFreq(freq); // Set frequency to 60 Hz
		int servoMin = 122; // 130;   // was 150. Min pulse length out of 4096
		int servoMax = 615;   // was 600. Max pulse length out of 4096

		final int CONTINUOUS_SERVO_CHANNEL = 14;
		final int STANDARD_SERVO_CHANNEL = 15;

		for (int i = 0; false && i < 5; i++) {
			System.out.println("i=" + i);
			servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, servoMin);
			servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, servoMin);
			delay(1_000);
			servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, servoMax);
			servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, servoMax);
			delay(1_000);
		}
		servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, 0); // Stop the continuous one
		servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, 0);   // Stop the standard one
		System.out.println("Done with the demo.");

		for (int i = servoMin; i <= servoMax; i++) {
			System.out.println("i=" + i);
			servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, i);
			delay(10);
		}
		for (int i = servoMax; i >= servoMin; i--) {
			System.out.println("i=" + i);
			servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, i);
			delay(10);
		}

		servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, 0); // Stop the continuous one
		servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, 0);   // Stop the standard one

		for (int i = servoMin; i <= servoMax; i++) {
			System.out.println("i=" + i);
			servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, i);
			delay(100);
		}
		for (int i = servoMax; i >= servoMin; i--) {
			System.out.println("i=" + i);
			servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, i);
			delay(100);
		}

		servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, 0); // Stop the continuous one
		servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, 0);   // Stop the standard one
		System.out.println("Done with the demo.");


		if (false) {
			System.out.println("Now, servoPulse");
			servoBoard.setPWMFreq(250);
			// The same with setServoPulse
			for (int i = 0; i < 5; i++) {
				servoBoard.setServoPulse(STANDARD_SERVO_CHANNEL, 1f);
				servoBoard.setServoPulse(CONTINUOUS_SERVO_CHANNEL, 1f);
				delay(1_000);
				servoBoard.setServoPulse(STANDARD_SERVO_CHANNEL, 2f);
				servoBoard.setServoPulse(CONTINUOUS_SERVO_CHANNEL, 2f);
				delay(1_000);
			}
			// Stop, Middle
			servoBoard.setServoPulse(STANDARD_SERVO_CHANNEL, 1.5f);
			servoBoard.setServoPulse(CONTINUOUS_SERVO_CHANNEL, 1.5f);

			servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, 0); // Stop the continuous one
		}
	}
}
