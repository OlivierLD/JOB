package job.devices;

import job.io.GPIO;
import job.io.JOBNativeInterface;
import utils.MiscUtils;
import utils.StringUtils;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/*
 * HC-SR04, Ultrasonic range sensor
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
       |  22 |  03 | GPIO_3       | #15 || #16 |       GPIO_4 | 04  | 23  |  Default trigger pin (out)
       |     |     | 3v3          | #17 || #18 |       GPIO_5 | 05  | 24  |  Default echo pin (in)
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
 *
 * Pin numbers for method of the GPIO class are BCM numbers.
 */
public class HC_SR04 {

	private final static Format DF22 = new DecimalFormat("#0.00");
	private static boolean DEBUG = "true".equals(System.getProperty("hc_sr04.verbose"));

	private final static int DEFAULT_TRIGGER_PIN = 23;
	private final static int DEFAULT_ECHO_PIN    = 24;

	private int trigPin, echoPin;
	private boolean simulating = JOBNativeInterface.isSimulated();

	private final static double SOUND_SPEED = 34_300d;       // in cm, 343.00 m/s
	private final static double DIST_FACT = SOUND_SPEED / 2; // round trip
	private final static int MIN_DIST = 3; // in cm

	private final static long BILLION = (long) 1E9;
	private final static int TEN_MICRO_SEC = 10_000; // In Nano secs

	public HC_SR04() {
		this(DEFAULT_TRIGGER_PIN, DEFAULT_ECHO_PIN);
	}

	public HC_SR04(int trig, int echo) {

		this.trigPin = trig;
		this.echoPin = echo;

		if ("true".equals(System.getProperty("gpio.verbose"))) {
			System.out.println(String.format("GPIO> Opening GPIO (%s)", this.getClass().getName()));
		}

		if (simulating) {
			if ("true".equals(System.getProperty("gpio.verbose"))) {
				System.out.println(String.format("GPIO> Will simulate (for %s)", this.getClass().getName()));
			}
		} else {
			GPIO.pinMode(this.trigPin, GPIO.OUTPUT);
			GPIO.pinMode(this.echoPin, GPIO.INPUT);
			GPIO.digitalWrite(this.trigPin, false); // LOW
			if (DEBUG) {
				System.out.println(String.format(">> Constructor."));
			}
		}
	}

	public int getTrigPin() {
		return this.trigPin;
	}

	public int getEchoPin() {
		return this.echoPin;
	}

	private final static long MAX_WAIT = 100; // 100ms = 1/10 of sec.
	private static boolean tooLong(long startedAt) {
		if ((System.currentTimeMillis() - startedAt) < MAX_WAIT) {
			return false;
		} else {
			if (DEBUG) {
				System.out.println(String.format("Echo took too long!! (more than %d \u03bcs", MAX_WAIT));
			}
			return true;
		}
	}

	public double readDistance() {
		double distance = -1L;
		GPIO.digitalWrite(this.trigPin, false); // Low
//	TimeUtil.delay(500L);

		// Just to check...
		int echoPinStatus = GPIO.digitalRead(this.echoPin);
		if (echoPinStatus == GPIO.HIGH) {
			System.out.println(">>> !! Before sending signal, echo PIN is High");
		}
		GPIO.digitalWrite(this.trigPin, true); // HIGH
		// 10 microsec to trigger the module  (8 ultrasound bursts at 40 kHz)
		// https://www.dropbox.com/s/615w1321sg9epjj/hc-sr04-ultrasound-timing-diagram.png
		MiscUtils.delay(0, TEN_MICRO_SEC);
		GPIO.digitalWrite(this.trigPin, false);

		// Wait for the signal to return
		long now = System.currentTimeMillis();
		while (GPIO.digitalRead(this.echoPin) == GPIO.LOW && !tooLong(now)); // && (start == 0 || (start != 0 && (start - top) < BILLION)))
		long start = System.nanoTime();
		// There it is, the echo comes back.
		now = System.currentTimeMillis();
		while (GPIO.digitalRead(this.echoPin) == GPIO.HIGH && !tooLong(now));
		long end = System.nanoTime();

		//  System.out.println(">>> TOP: start=" + start + ", end=" + end);
		double travelTime = (end - start);
		if (travelTime > 0) { //  && start > 0)
			double pulseDuration = travelTime / (double) BILLION; // in seconds
			distance = pulseDuration * DIST_FACT;
			if (DEBUG) {
//			System.out.println(String.format("TravelTime: %d \u00e5s (nano sec), pulseDuration: %s", travelTime, DF_N.format(pulseDuration)));
				if (distance < 1_000) { // Less than 10 meters, in cm.
					System.out.println(String.format("Distance: %s cm. Duration: %s \u00e5s", DF22.format(distance), NumberFormat.getInstance().format(travelTime))); // + " (" + pulseDuration + " = " + end + " - " + start + ")");
				} else {
					System.out.println("   >>> Too far:" + DF22.format(distance) + " cm.");
				}
			}
		} else {
			throw new RuntimeException("Hiccup! start:" + NumberFormat.getInstance().format(start) + ", end:" + NumberFormat.getInstance().format(end));
		}
		return distance;
	}
}
