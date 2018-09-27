package examples.jobio.servo;

import job.io.SoftwareServo;

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

	/**
	 * Red on 3v3 (physical #1),
	 * Black on GND
	 * @param args int, BCM number of the PWM wire
	 *
	+-----+-----+--------------+-----++-----+--------------+-----+-----+
	| BCM | wPi | Name         |  Physical  |         Name | wPi | BCM |
	+-----+-----+--------------+-----++-----+--------------+-----+-----+
	|     |     | 3v3          | #01 || #02 |          5v0 |     |     |
	|  02 |  08 | SDA1         | #03 || #04 |          5v0 |     |     |
	|  03 |  09 | SCL1         | #05 || #06 |          GND |     |     |
	|  04 |  07 | GPCLK0       | #07 || #08 |    UART0_TXD | 15  | 14  |
	|     |     | GND          | #09 || #10 |    UART0_RXD | 16  | 15  |
	|  17 |  00 | GPIO_0       | #11 || #12 | PCM_CLK/PWM0 | 01  | 18  |
	|  27 |  02 | GPIO_2       | #13 || #14 |          GND |     |     |
	|  22 |  03 | GPIO_3       | #15 || #16 |       GPIO_4 | 04  | 23  |
	|     |     | 3v3          | #01 || #18 |       GPIO_5 | 05  | 24  |
	|  10 |  12 | SPI0_MOSI    | #19 || #20 |          GND |     |     |
	|  09 |  13 | SPI0_MISO    | #21 || #22 |       GPIO_6 | 06  | 25  |
	|  11 |  14 | SPI0_CLK     | #23 || #24 |   SPI0_CS0_N | 10  | 08  |
	|     |     | GND          | #25 || #26 |   SPI0_CS1_N | 11  | 07  |
	|     |  30 | SDA0         | #27 || #28 |         SCL0 | 31  |     |
	|  05 |  21 | GPCLK1       | #29 || #30 |          GND |     |     |
	|  06 |  22 | GPCLK2       | #31 || #32 |         PWM0 | 26  | 12  |
	|  13 |  23 | PWM1         | #33 || #34 |          GND |     |     |
	|  19 |  24 | PCM_FS/PWM1  | #35 || #36 |      GPIO_27 | 27  | 16  |
	|  26 |  25 | GPIO_25      | #37 || #38 |      PCM_DIN | 28  | 20  |
	|     |     | GND          | #39 || #40 |     PCM_DOUT | 29  | 21  |
	+-----+-----+--------------+-----++-----+--------------+-----+-----+
	| BCM | wPi | Name         |  Physical  |         Name | wPi | BCM |
	+-----+-----+--------------+-----++-----+--------------+-----+-----+
	 */
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
