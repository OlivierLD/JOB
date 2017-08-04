package  jhard.devices;

import jhard.io.I2C;

// SSD1306 is a small, inexpensive 128x32 pixels monochrome OLED display
// available online as "0.96" 128x32 OLED display", SKU 346540
// or from Adafruit
// datasheet: https://www.adafruit.com/datasheets/SSD1306.pdf

public class SSD1306 extends I2C {

	public final static int SSD1306_I2C_ADDRESS                          = 0x3C; // 011110+SA0+RW - 0x3C or 0x3D
	public final static int SSD1306_SETCONTRAST                          = 0x81;
	public final static int SSD1306_DISPLAYALLON_RESUME                  = 0xA4;
	public final static int SSD1306_DISPLAYALLON                         = 0xA5;
	public final static int SSD1306_NORMALDISPLAY                        = 0xA6;
	public final static int SSD1306_INVERTDISPLAY                        = 0xA7;
	public final static int SSD1306_DISPLAYOFF                           = 0xAE;
	public final static int SSD1306_DISPLAYON                            = 0xAF;
	public final static int SSD1306_SETDISPLAYOFFSET                     = 0xD3;
	public final static int SSD1306_SETCOMPINS                           = 0xDA;
	public final static int SSD1306_SETVCOMDETECT                        = 0xDB;
	public final static int SSD1306_SETDISPLAYCLOCKDIV                   = 0xD5;
	public final static int SSD1306_SETPRECHARGE                         = 0xD9;
	public final static int SSD1306_SETMULTIPLEX                         = 0xA8;
	public final static int SSD1306_SETLOWCOLUMN                         = 0x00;
	public final static int SSD1306_SETHIGHCOLUMN                        = 0x10;
	public final static int SSD1306_SETSTARTLINE                         = 0x40;
	public final static int SSD1306_MEMORYMODE                           = 0x20;
	public final static int SSD1306_COLUMNADDR                           = 0x21;
	public final static int SSD1306_PAGEADDR                             = 0x22;
	public final static int SSD1306_COMSCANINC                           = 0xC0;
	public final static int SSD1306_COMSCANDEC                           = 0xC8;
	public final static int SSD1306_SEGREMAP                             = 0xA0;
	public final static int SSD1306_CHARGEPUMP                           = 0x8D;
	public final static int SSD1306_EXTERNALVCC                          = 0x01;
	public final static int SSD1306_SWITCHCAPVCC                         = 0x02;

	// Scrolling constants
	public final static int SSD1306_ACTIVATE_SCROLL                      = 0x2F;
	public final static int SSD1306_DEACTIVATE_SCROLL                    = 0x2E;
	public final static int SSD1306_SET_VERTICAL_SCROLL_AREA             = 0xA3;
	public final static int SSD1306_RIGHT_HORIZONTAL_SCROLL              = 0x26;
	public final static int SSD1306_LEFT_HORIZONTAL_SCROLL               = 0x27;
	public final static int SSD1306_VERTICAL_AND_RIGHT_HORIZONTAL_SCROLL = 0x29;
	public final static int SSD1306_VERTICAL_AND_LEFT_HORIZONTAL_SCROLL  = 0x2A;

  public final static int DEFAULT_ADDR = SSD1306_I2C_ADDRESS;
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
    this.writeCommand(SSD1306_DISPLAYOFF);                // 0xAE, turn display off
	  this.writeCommand(SSD1306_SETDISPLAYCLOCKDIV, 0x80);  // 0xD5, set display clock divide ratio & oscillator frequency to default
	  this.writeCommand(SSD1306_SETMULTIPLEX, 0x1f);        // 0xA8, set multiplex ratio to the highest setting, was 0x3F
	  this.writeCommand(SSD1306_SETDISPLAYOFFSET, 0x00);    // 0xD3, no display offset
	  this.writeCommand(SSD1306_SETSTARTLINE | 0x00);       // 0x40, set default display start line
    this.writeCommand(SSD1306_CHARGEPUMP, 0x14);          // 0x8D, enable charge pump
    this.writeCommand(SSD1306_MEMORYMODE, 0x00);          // 0x20, set memory addressing mode to horizontal
	  this.writeCommand(SSD1306_SEGREMAP | 0x01);           // 0xA0, set segment re-map
	  this.writeCommand(SSD1306_COMSCANDEC);                // 0xC8, set COM output scan direction
	  this.writeCommand(SSD1306_SETCOMPINS, 0x02);          // 0xDA, set COM pins hardware configuration, was 0x12
	  this.writeCommand(SSD1306_SETCONTRAST, 0x8f);         // 0x81, set contrast, was 0xCF
	  this.writeCommand(SSD1306_SETPRECHARGE, 0xf1);        // 0xD9, set pre-charge period to 241x DCLK
	  this.writeCommand(SSD1306_SETVCOMDETECT, 0x40);       // 0xDB, set VCOMH deselect level
	  this.writeCommand(SSD1306_DISPLAYALLON_RESUME);       // 0xA4, display RAM content (not all-on)
	  this.writeCommand(SSD1306_NORMALDISPLAY);             // 0xA6, set normal (not-inverted) display
  }

  public void invert(boolean inverted) {
    if (inverted) {
      writeCommand(SSD1306_INVERTDISPLAY); // 0xA7
    } else {
      writeCommand(SSD1306_NORMALDISPLAY); // 0xA6
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
			throw new RuntimeException(String.format("Invalid buffer length %d, should be %d (%d * %d)", len, (w * 2 * (h / 8)), w, h));
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

  // TODO Object: PImage
  public void sendImage(Object img, int startX, int startY) {
    byte[] frame = new byte[512];
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
//  if (buf.length != 1024) {
    if (buf.length % 512 != 0) {
      System.err.println(String.format("The framebuffer should be 1024 bytes long (found %d), with one bit per pixel", buf.length));
      throw new IllegalArgumentException("Unexpected buffer size");
    }

	  this.writeCommand(SSD1306_COLUMNADDR, 0, 127);        // 0x21, set column address
	  // Page size: (h / 8) - 1;
//  this.writeCommand(SSD1306_PAGEADDR, 0, 7);            // 0x22, set page address. 8 lines
	  this.writeCommand(SSD1306_PAGEADDR, 0, 3);            // 0x22, set page address. 4 lines

//  this.writeCommand(SSD1306_DEACTIVATE_SCROLL);         // 0x2E, deactivate scroll
//  this.writeCommand(SSD1306_DISPLAYON);                 // 0xAF, turn display on

//  this.writeCommand(SSD1306_SETLOWCOLUMN | 0x0);  // set start address
//  this.writeCommand(SSD1306_SETHIGHCOLUMN | 0x0); // set higher column start address
//  this.writeCommand(SSD1306_SETSTARTLINE | 0x0);  // set start line

    // send the frame buffer as 16 byte long packets
    for (int i=0; i < buf.length/16; i++) {
      super.beginTransmission(address);
      super.write(SSD1306_SETSTARTLINE);  // 0x40, indicates data write
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
    super.write(SSD1306_SETLOWCOLUMN);    // 0x00, indicates command write
    super.write(arg1);
    super.endTransmission();
  }

  protected void writeCommand(int arg1, int arg2) {
    super.beginTransmission(address);
    super.write(SSD1306_SETLOWCOLUMN);
    super.write(arg1);
    super.write(arg2);
    super.endTransmission();
  }

  protected void writeCommand(int arg1, int arg2, int arg3) {
    super.beginTransmission(address);
    super.write(SSD1306_SETLOWCOLUMN);
    super.write(arg1);
    super.write(arg2);
    super.write(arg3);
    super.endTransmission();
  }
}
