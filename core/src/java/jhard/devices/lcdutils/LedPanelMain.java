package jhard.devices.lcdutils;

import jhard.devices.SSD1306;
import jhard.devices.lcdutils.img.ImgInterface;
import jhard.devices.lcdutils.img.Java32x32;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import utils.StringUtils;
import static utils.MiscUtils.delay;


/**
 * A Standalone program, simulating an LCD, with Swing.
 */
public class LedPanelMain
		extends java.awt.Frame {
	private LedPanelMain instance = this;
	private LEDPanel ledPanel;
	private JPanel bottomPanel;
	private JCheckBox gridCheckBox;
	private JButton againButton;

	private transient ScreenBuffer sb;

	// SSD1306
	private final static int NB_LINES = 32;
	private final static int NB_COLS = 128;
	// Nokia
//  private final static int NB_LINES = 48;
//  private final static int NB_COLS  = 84;

	public LedPanelMain() {
		initComponents();
		this.setSize(new Dimension(1_000, 300));
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents() {
		ledPanel = new LEDPanel(NB_LINES, NB_COLS);

		ledPanel.setWithGrid(false);

		setPreferredSize(new java.awt.Dimension(1_000, 600));
		setTitle("LCD Screen Buffer");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});
		add(ledPanel, java.awt.BorderLayout.CENTER);

		bottomPanel = new JPanel();
		gridCheckBox = new JCheckBox("With Grid");
		gridCheckBox.setSelected(false);
		bottomPanel.add(gridCheckBox, null);
		gridCheckBox.addActionListener(actionEvent -> {
				ledPanel.setWithGrid(gridCheckBox.isSelected());
				ledPanel.repaint();
			});
		againButton = new JButton("Play again");
		bottomPanel.add(againButton, null);
		againButton.addActionListener(actionEvent -> {
				Thread go = new Thread(() -> {
						instance.doYourJob();
					});
				go.start();
			});

		add(bottomPanel, java.awt.BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Simulator. Takes the screenbuffer expected by the real device and displays it on
	 * a led array (2 dims).
	 *
	 * @param screenbuffer as expected by the device.
	 */
	private void setBuffer(byte[] screenbuffer) {
		if ("true".equals(System.getProperty("dump.screen","false"))) {
			FrameDump.dump(screenbuffer);
		}
		// This displays the buffer top to bottom, instead of left to right
		char[][] screenMatrix = new char[NB_LINES][NB_COLS];
		for (int col = 0; col < NB_COLS; col++) {
			// Line is a VERTICAL line of the screen, its length is NB_LINES (32)
			String screenVerticalLine = "";
			for (int l = (NB_LINES / 8) - 1; l >= 0; l--) {
				screenVerticalLine += StringUtils.lpad(Integer.toBinaryString(screenbuffer[col + (l * NB_COLS)] & 0xFF), 8, "0").replace('0', ' ').replace('1', 'X');
			}
			if ("true".equals(System.getProperty("dump.screen","false"))) {
				System.out.println(screenVerticalLine);
			}
			for (int row = 0; row < screenVerticalLine.length(); row++) {
				try {
					char mc = screenVerticalLine.charAt(row);
					//           Line
					//           |    Column
					//           |    |
					screenMatrix[row][col] = mc;
				} catch (Exception ex) {
					System.err.println("Line:" + screenVerticalLine + " (" + screenVerticalLine.length() + " character(s))");
					System.err.println("r:" + row + ", c=" + col + ", buffer length:" + screenbuffer.length);
					ex.printStackTrace();
				}
			}
		}
		// Display the screen matrix, as it should be seen
		boolean[][] ledMatrix = ledPanel.getLedOnOff();
		for (int line = 0; line < NB_LINES; line++) {
			for (int col = 0; col < NB_COLS; col++) {
				ledMatrix[NB_LINES - 1 - line][col] = screenMatrix[line][col] == 'X';
			}
		}
		if ("true".equals(System.getProperty("dump.screen","false"))) {
			System.out.println("--- LED Matrix ---");
			// Dump LED Matrix
			for (int line = 0; line < NB_LINES; line++) {
				final int fLine = line;
				String displayLine = IntStream.range(0, NB_COLS)
						.boxed()
						.map((Integer idx) -> ledMatrix[fLine][idx] ? "X" : " ")
						.collect(Collectors.joining(""));
				System.out.println(displayLine);
			}
		}
		ledPanel.setLedOnOff(ledMatrix);
	}

	private void display() {
		ledPanel.repaint();
	}

	public void doYourJob() {
		LedPanelMain lcd = instance;
		againButton.setEnabled(false);
//  instance.repaint();
		if (true) {
			if (sb == null) {
				sb = new ScreenBuffer(); // NB_COLS, NB_LINES
				sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
			}

			if (true) {
				sb.text("ScreenBuffer", 2, 9, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text(NB_COLS + " x " + NB_LINES + " for LCD", 2, 19, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("I speak Java!", 2, 29, ScreenBuffer.Mode.BLACK_ON_WHITE);

				lcd.setBuffer(sb.getScreenBuffer());

				lcd.display();
//      sb.dumpScreen();
				delay(5_000);

				sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
				ImgInterface img = new Java32x32();
				sb.image(img, 0, 0, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("I speak Java!", 36, 20, ScreenBuffer.Mode.BLACK_ON_WHITE);

				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(5_000);

				byte[] mirror = SSD1306.mirror(sb.getScreenBuffer(), NB_COLS, NB_LINES);

				lcd.setBuffer(mirror);

				lcd.display();
				delay(5_000);

				// Bigger
				sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("Pi = ", 2, 9, 1, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("3.1415926\u00b0", 2, 19, 2, ScreenBuffer.Mode.WHITE_ON_BLACK); // With a useless degree symbol (for tests).
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(5_000);

				// Blinking
				sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("ScreenBuffer", 2, 9, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text(NB_COLS + " x " + NB_LINES + " for LCD", 2, 19, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("I speak Java!", 2, 29, ScreenBuffer.Mode.WHITE_ON_BLACK);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(50);

				sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("ScreenBuffer", 2, 9, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text(NB_COLS + " x " + NB_LINES + " for LCD", 2, 19, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("I speak Java!", 2, 29, ScreenBuffer.Mode.BLACK_ON_WHITE);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(50);

				sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("ScreenBuffer", 2, 9, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text(NB_COLS + " x " + NB_LINES + " for LCD", 2, 19, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("I speak Java!", 2, 29, ScreenBuffer.Mode.WHITE_ON_BLACK);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(50);

				sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("ScreenBuffer", 2, 9, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text(NB_COLS + " x " + NB_LINES + " for LCD", 2, 19, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("I speak Java!", 2, 29, ScreenBuffer.Mode.BLACK_ON_WHITE);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(50);

				sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("ScreenBuffer", 2, 9, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text(NB_COLS + " x " + NB_LINES + " for LCD", 2, 19, ScreenBuffer.Mode.WHITE_ON_BLACK);
				sb.text("I speak Java!", 2, 29, ScreenBuffer.Mode.WHITE_ON_BLACK);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(2_000);
				// End blinking
			}

			if (false) {
				String[] txt1 = new String[]{
						"!\":#$%&'()*+,-./01234",
						"56789;<=>?@ABCDEFGHI",
						"JKLMNOPQRSTUVWXYZ[\\]"
				};
				String[] txt2 = new String[]{
						"^_abcdefghijklmnopqr",
						"stuvwxyz{|}"
				};

				boolean one = false;

				for (int t = 0; t < 4; t++) {
					sb.clear();
					String[] sa = one ? txt1 : txt2;
					for (int i = 0; i < sa.length; i++)
						sb.text(sa[i], 0, 10 + (i * 10));
					lcd.setBuffer(sb.getScreenBuffer());
					lcd.display();
					one = !one;
					delay(2_000);
				}
			}

			// Image + text, marquee
			if (true) {
				sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
				ImgInterface img = new Java32x32();
				sb.image(img, 0, 0, ScreenBuffer.Mode.BLACK_ON_WHITE);
				sb.text("I speak Java!", 36, 20, ScreenBuffer.Mode.BLACK_ON_WHITE);

				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(2_000);

				sb.clear();
				for (int x = 0; x < 128; x++) {
					sb.image(img, 0 - x, 0);
					sb.text("I speak Java!.....", 36 - x, 20);

					lcd.setBuffer(sb.getScreenBuffer());
					lcd.display();
					long s = (long) (150 - (1.5 * x));
					delay(s > 0 ? s : 0);
				}
			}

			// Circles
			if (true) {
				sb.clear();
				sb.circle(64, 16, 15);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.circle(74, 16, 10);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.circle(80, 16, 5);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Lines
			if (true) {
				sb.clear();
				sb.line(1, 1, 126, 30);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.line(126, 1, 1, 30);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.line(1, 25, 120, 10);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.line(10, 5, 10, 30);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.line(1, 5, 120, 5);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Rectangle
			if (true) {
				sb.clear();
				sb.rectangle(5, 10, 100, 25);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);

				sb.rectangle(15, 3, 50, 30);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Nested rectangles
			if (true) {
				sb.clear();
				for (int i = 0; i < 8; i++) {
					sb.rectangle(1 + (i * 2), 1 + (i * 2), 127 - (i * 2), 31 - (i * 2));
					lcd.setBuffer(sb.getScreenBuffer());
					lcd.display();
					delay(100);
				}
				delay(1_000);
			}

			// Arc
			if (true) {
				sb.clear();
				sb.arc(64, 16, 10, 20, 90);
				sb.plot(64, 16);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Shape
			if (true) {
				sb.clear();
				int[] x = new int[]{64, 73, 50, 78, 55};
				int[] y = new int[]{1, 30, 12, 12, 30};
				Polygon p = new Polygon(x, y, 5);
				sb.shape(p, true);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Centered text
			if (true) {
				sb.clear();
				String txt = "Centered";
				int len = sb.strlen(txt);
				sb.text(txt, 64 - (len / 2), 16);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
				// sb.clear();
				txt = "A much longer string.";
				len = sb.strlen(txt);
				sb.text(txt, 64 - (len / 2), 26);
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Vertical marquee
			if (true) {
				String[] txt = new String[]{
						"Centered",
						"This is line one",
						"More text goes here",
						"Some crap follows: ...",
						"We're reaching the end",
						"* The End *"
				};
				int len = 0;
				for (int t = 0; t < 80; t++) {
					sb.clear();
					for (int i = 0; i < txt.length; i++) {
						len = sb.strlen(txt[i]);
						sb.text(txt[i], 64 - (len / 2), (10 * (i + 1)) - t);
						lcd.setBuffer(sb.getScreenBuffer());
						lcd.display();
					}
					delay(100);
				}
//      sb.dumpScreen();

				delay(1_000);
			}

			if (true) {
				// Text Snake...
				String snake = "This text is displayed like a snake, waving across the screen...";
				char[] ca = snake.toCharArray();
				int strlen = sb.strlen(snake);
				// int i = 0;
				for (int i = 0; i < strlen + 2; i++) {
					sb.clear();
					for (int c = 0; c < ca.length; c++) {
						int strOffset = 0;
						if (c > 0) {
							String tmp = new String(ca, 0, c);
							//    System.out.println(tmp);
							strOffset = sb.strlen(tmp) + 2;
						}
						double virtualAngle = Math.PI * (((c - i) % 32) / 32d);
						int x = strOffset - i,
								y = 26 + (int) (16 * Math.sin(virtualAngle));
//          System.out.println("Displaying " + ca[c] + " at " + x + ", " + y + ", i=" + i + ", strOffset=" + strOffset);
						sb.text(new String(new char[]{ca[c]}), x, y);
					}
					lcd.setBuffer(sb.getScreenBuffer());
					lcd.display();
					delay(75);
				}
			}

			// Curve
			if (true) {
				sb.clear();
				// Axis
				sb.line(0, 16, 128, 16);
				sb.line(2, 0, 2, 32);

				Point prev = null;
				for (int x = 0; x < 130; x++) {
					double amplitude = 6 * Math.exp((double) (130 - x) / (13d * 7.5d));
					//    System.out.println("X:" + x + ", ampl: " + (amplitude));
					int y = 16 - (int) (amplitude * Math.cos(Math.toRadians(360 * x / 16d)));
					sb.plot(x + 2, y);
					if (prev != null)
						sb.line(prev.x, prev.y, x + 2, y);
					prev = new Point(x + 2, y);
				}
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Progressing Curve
			if (true) {
				sb.clear();
				// Axis
				sb.line(0, 16, 128, 16);
				sb.line(2, 0, 2, 32);

				Point prev = null;
				for (int x = 0; x < 130; x++) {
					double amplitude = 6 * Math.exp((double) (130 - x) / (13d * 7.5d));
					//  System.out.println("X:" + x + ", ampl: " + (amplitude));
					int y = 16 - (int) (amplitude * Math.cos(Math.toRadians(360 * x / 16d)));
					sb.plot(x + 2, y);
					if (prev != null)
						sb.line(prev.x, prev.y, x + 2, y);
					prev = new Point(x + 2, y);
					lcd.setBuffer(sb.getScreenBuffer());
					lcd.display();
					delay(75);
				}
				lcd.setBuffer(sb.getScreenBuffer());
				lcd.display();
				delay(1_000);
			}

			// Bouncing
			if (true) {
				sb.clear();
				for (int x = 0; x < 130; x++) {
					sb.clear();
					double amplitude = 6 * Math.exp((double) (130 - x) / (13d * 7.5d));
					//  System.out.println("X:" + x + ", ampl: " + (amplitude));
					int y = 32 - (int) (amplitude * Math.abs(Math.cos(Math.toRadians(180 * x / 10d))));
					// 4 dots
					sb.plot(x, y);
					sb.plot(x + 1, y);
					sb.plot(x + 1, y + 1);
					sb.plot(x, y + 1);

					lcd.setBuffer(sb.getScreenBuffer());
					lcd.display();
					delay(75);
				}
				delay(1_000);
			}
			againButton.setEnabled(true);

			System.out.println("...Done!");
		}
	}

	/**
	 * Exit the Application
	 */
	private void exitForm(java.awt.event.WindowEvent evt) {
		System.out.println("Bye");
		System.exit(0);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String... args) {
		// Display available characters:
		Map<String, String[]> characters = CharacterMatrixes.characters;
		Set<String> keys = characters.keySet();
		List<String> kList = new ArrayList<String>(keys.size());
		for (String k : keys)
			kList.add(k);
		// Sort here
		Collections.sort(kList);
		for (String k : kList)
			System.out.print(k + " ");
		System.out.println();

		// Display led panel.
		LedPanelMain lp = new LedPanelMain();
		lp.setVisible(true);
		lp.doYourJob();
	}
}
