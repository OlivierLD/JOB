package  jhard.devices;

import jhard.io.I2C;

// SSD1306 is a small, inexpensive 128x64 pixels monochrome OLED display
// available online as "0.96" 128x64 OLED display", SKU 346540
// or from Adafruit
// datasheet: https://www.adafruit.com/datasheets/SSD1306.pdf

public class SSD1306 extends I2C {

  public final static int DEFAULT_ADDR = 0x3c;
  public final static String DEFAULT_BUS = "i2c-1";

  private int address;
  private byte[] buffer = null;

  // there can be more than one device connected to the bus
  // as long as they have different addresses
  public SSD1306() {
    this(DEFAULT_BUS, DEFAULT_ADDR);
  }
  public SSD1306(int addr) {
    this(DEFAULT_BUS, addr);
  }
  public SSD1306(String bus) {
    this(bus, DEFAULT_ADDR);
  }
  public SSD1306(String bus, int address) {
    super(bus);
    this.address = address;
    init();
  }

  public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public byte[] getBuffer() {
		return buffer;
	}

  protected void init() {
    writeCommand(0xae);         // turn display off
    writeCommand(0xa8, 0x3f);   // set multiplex ratio to the highest setting
    writeCommand(0x8d, 0x14);   // enable charge pump
    writeCommand(0x20, 0x00);   // set memory addressing mode to horizontal
    writeCommand(0xd5, 0x80);   // set display clock divide ratio & oscillator frequency to default
    writeCommand(0xd3, 0x00);   // no display offset
    writeCommand(0x40 | 0x00);  // set default display start line

    // use the following two lines to flip the display
    writeCommand(0xa0 | 0x01);  // set segment re-map
    writeCommand(0xc8);         // set COM output scan direction

    writeCommand(0xda, 0x12);   // set COM pins hardware configuration
    writeCommand(0xd9, 0xf1);   // set pre-charge period to 241x DCLK
    writeCommand(0xdB, 0x40);   // set VCOMH deselect level
    writeCommand(0xa4);         // display RAM content (not all-on)
    writeCommand(0xa6);         // set normal (not-inverted) display

    // set this since we don't have access to the OLED's reset pins (?)
    writeCommand(0x21, 0, 127); // set column address
    writeCommand(0x22, 0, 7);   // set page address

    writeCommand(0x81, 0xcf);   // set contrast
    writeCommand(0x2e);         // deactivate scroll
    writeCommand(0xaf);         // turn display on
  }

  public void invert(boolean inverted) {
    if (inverted) {
      writeCommand(0xa7);
    } else {
      writeCommand(0xa6);
    }
  }

  /**
	 * Use if the screen is to be seen in a mirror.
	 * Left and right are inverted.
	 *
	 * @param buff the screen buffer to invert
	 * @param w    width (in pixels) of the above
	 * @param h    height (in pixels) of the above. One row has 8 pixels.
	 * @return the mirrored buffer.
	 */
	public static byte[] mirror(byte[] buff, int w, int h) {
		int len = buff.length;
		if (len != w * (h / 8)) {
			throw new RuntimeException(String.format("Invalid buffer length %d, should be %d (%d * %d)", len, (w * (h / 8)), w, h));
		}
		byte[] mirror = new byte[len];
		for (int row = 0; row < (h / 8); row++) {
			for (int col = 0; col < w; col++) {
				int buffIdx = (row * w) + col;
				int mirrorBuffIdx = (row * w) + (w - col - 1);
				mirror[mirrorBuffIdx] = buff[buffIdx];
			}
		}
		return mirror;
	}

  // TODO Object: PImage
  public void sendImage(Object img) {
    sendImage(img, 0, 0);
  }

  // TODO Obejct: PImage
  public void sendImage(Object img, int startX, int startY) {
    byte[] frame = new byte[1024];
    // img.loadPixels();
    // for (int y=startY; y < height && y-startY < 64; y++) {
    //   for (int x=startX; x < width && x-startX < 128; x++) {
    //     if (128 <= brightness(img.pixels[y*img.width+x])) {
    //       // this isn't the normal (scanline) mapping, but 8 pixels below each other at a time
    //       // white pixels have their bit turned on
    //       frame[x + (y/8)*128] |= (1 << (y % 8));
    //     }
    //   }
    // }
    sendFramebuffer(frame);
  }

  public void sendFramebuffer(byte[] buf) {
    if (buf.length != 1024) {
      System.err.println(String.format("The framebuffer should be 1024 bytes long (found %d), with one bit per pixel", buf.length));
      throw new IllegalArgumentException("Unexpected buffer size");
    }

    writeCommand(0x00 | 0x0); // set start address
    writeCommand(0x10 | 0x0); // set higher column start address
    writeCommand(0x40 | 0x0); // set start line

    // send the frame buffer as 16 byte long packets
    for (int i=0; i < buf.length/16; i++) {
      super.beginTransmission(address);
      super.write(0x40);  // indicates data write
      for (int j=0; j < 16; j++) {
        super.write(buf[i*16+j]);
      }
      super.endTransmission();
    }
  }

  public void display() {
    this.sendFramebuffer(this.buffer);
  }

  protected void writeCommand(int arg1) {
    super.beginTransmission(address);
    super.write(0x00);    // indicates command write
    super.write(arg1);
    super.endTransmission();
  }

  protected void writeCommand(int arg1, int arg2) {
    super.beginTransmission(address);
    super.write(0x00);
    super.write(arg1);
    super.write(arg2);
    super.endTransmission();
  }

  protected void writeCommand(int arg1, int arg2, int arg3) {
    super.beginTransmission(address);
    super.write(0x00);
    super.write(arg1);
    super.write(arg2);
    super.write(arg3);
    super.endTransmission();
  }
}
