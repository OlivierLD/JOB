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

		// Init
		servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, 0); // Stop the continuous one
		servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, 0);   // Stop the standard one

		Thread one = new Thread(() -> {
			int pos = servoMin;
			int sign = 1;
			while (true) {
				servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, pos);
				pos += (sign);
				if (pos > servoMax || pos < servoMin) {
					sign *= -1;
					pos += (sign);
				}
				delay(10);
			}
		});

		Thread two = new Thread(() -> {
			int pos = servoMin;
			int sign = 1;
			while (true) {
				servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, pos);
				pos += (sign);
				if (pos > servoMax || pos < servoMin) {
					sign *= -1;
					pos += (sign);
				}
				delay(100);
			}
		});

    one.start();
    two.start();
    
	}
}
