import processing.io.*;

/**
 * With a Graphical User Interface
 */
BME280 bme280;

int centerX = 200;
int centerY = 200;
int tubeBottom = 380;
int intRadius =  20;
int extRadius = 180;


void setup() {
	size(1000, 400);
	noStroke();
	noFill();
	textSize(10);
  System.setProperty("bme280.verbose", "false");
	bme280 = new BME280();
}

int pressureFrom = 973, pressureTo = 1053; // 1013 is centered.
int humidityFrom = 0, humidityTo = 100;
int temperatureFrom = -20, temperatureTo = 50;

void draw() {
	background(0);
  try {
	  float temp = bme280.readTemperature();
	  float hum = bme280.readHumidity();
	  float press = bme280.readPressure();

		drawDisplay(press / 100f,
            		pressureFrom,
            		pressureTo,
            		10,
            		1,
            		0,
            		0,
            		color(255, 204, 0), // Goldish
            		"hPa",
            		"Pressure");

		drawDisplay(hum,
            		humidityFrom,
            		humidityTo,
            		10,
            		1,
            		2 * centerX,
            		0,
            		color(0, 255, 255), // Cyan
            		"%",
            		"Humidity");

		drawTube(temp,
						 temperatureFrom,
						 temperatureTo,
             20,                 // tube width
             270,                // tube height
             (4 * centerX) + 40, // X Offset
             0,                  // Y Offset
             color(0, 255, 0),   // tick color, green.
             1,                  // minor
             5,                  // Major
             "\272C");           // Unit
	} catch (Exception ex) {
    ex.printStackTrace();
  }
}

/**
 * Generic.
 * Provide: value, from, to, minor ticks, major ticks, xOffset, yOffset
 */
void drawDisplay(float value, int from, int to, int majorTicks, int minorTicks, int xOffset, int yOffset, color c, String unit, String label) {
  float sectorToCover = 260f; // On 260 degrees
  textSize(10);
  fill(255);
  text(String.format("%05.2f %s", value, unit), 5 + xOffset, 12);

  // The ticks
  stroke(c);
  for (int p=from; p<=to; p+=minorTicks) {
    strokeWeight((p % majorTicks == 0 ? 3 : 1));
    float _p = ((p - ((from + to) / 2)) * (sectorToCover / (to - from))) - 90f;
    line(centerX + xOffset + ((extRadius + 5) * (float)Math.cos(Math.toRadians(_p))),
         centerY + yOffset + ((extRadius + 5) * (float)Math.sin(Math.toRadians(_p))),
         centerX + xOffset + ((extRadius - (p % majorTicks == 0 ? 20 : 10)) * (float)Math.cos(Math.toRadians(_p))),
         centerY + yOffset + ((extRadius - (p % majorTicks == 0 ? 20 : 10)) * (float)Math.sin(Math.toRadians(_p))));
  }
  strokeWeight(1);
  stroke(255);

  // Label
  textSize(32);
  fill(c);
  float strW = textWidth(label);
  text(label, (centerX + xOffset) - (strW / 2), (centerY + yOffset) - (extRadius * .50) + 32);

  // Drawing the hand
  float _value = ((value - ((from + to) / 2)) * (sectorToCover / (to - from))) - 90f;
//println(String.format("Pressure: %.02f, Angle: %f", pressure, _pressure));
  fill(255); // White
  triangle(centerX + xOffset,
           centerY + yOffset,
           (float)(centerX + xOffset + (extRadius * Math.cos(Math.toRadians(_value)))),
           (float)(centerY + yOffset + (extRadius * Math.sin(Math.toRadians(_value)))),
           (float)(centerX + xOffset + (intRadius * Math.cos(Math.toRadians(_value + 45)))),
           (float)(centerY + yOffset + (intRadius * Math.sin(Math.toRadians(_value + 45)))));
  fill(128); // Gray
  triangle(centerX + xOffset,
           centerY + yOffset,
           (float)(centerX + xOffset + (extRadius * Math.cos(Math.toRadians(_value)))),
           (float)(centerY + yOffset + (extRadius * Math.sin(Math.toRadians(_value)))),
           (float)(centerX + xOffset + (intRadius * Math.cos(Math.toRadians(_value - 45)))),
           (float)(centerY + yOffset + (intRadius * Math.sin(Math.toRadians(_value - 45)))));

  // knob
  fill(180); // Grey'ish
  ellipse(centerX + xOffset, centerY + yOffset, intRadius * 2, intRadius * 2);

  // print the Value
  textSize(32);
  fill(c);
  String str = String.format("%05.2f %s", value, unit);
  strW = textWidth(str);
  text(str, (centerX + xOffset) - (strW / 2), (centerY + yOffset) + (extRadius * .50));
}

void drawTube(float value, int from, int to, int tubeWidth, int tubeHeight, int xOffset, int yOffset, color tickColor, int minor, int major, String unit) {
  // Ticks here
  fill(tickColor);
  strokeWeight(1);
  stroke(tickColor);
  for (int t=from; t<=to; t+=minor) {
    float y = tubeBottom + yOffset - (tubeWidth * 1.5) - ((t - from) * (float)tubeHeight / (float)(to - from));
    strokeWeight(t == 0 ? 3 : 1);
    line(xOffset - (t % major == 0 ? tubeWidth : (tubeWidth * 0.75)),
         y,
         xOffset + (t % major == 0 ? tubeWidth : (tubeWidth * 0.75)),
         y);
    if (t == 0) {
      textSize(12);
      text("0" + unit, xOffset + (tubeWidth * 1.5), y + 4);
    }
  }

  fill(180);
  noStroke();
  ellipse(xOffset, tubeBottom + yOffset - (tubeWidth * 1.5), tubeWidth * 1.5, tubeWidth * 1.5); // Bottom
  ellipse(xOffset, tubeBottom + yOffset - (tubeWidth * 1.5) - tubeHeight, tubeWidth, tubeWidth); // Top
  rect(xOffset - (tubeWidth / 2), tubeBottom + yOffset - (tubeWidth * 1.5) - tubeHeight, tubeWidth, tubeHeight); // body
  fill(255, 0, 0);
  ellipse(xOffset, tubeBottom + yOffset - (tubeWidth * 1.5), tubeWidth * 1.1, tubeWidth * 1.1); // Bottom, red.
  // Value
  float _value = (value - from) * (float)tubeHeight / (float)(to - from);
  rect(xOffset - ((tubeWidth * 0.6) / 2), tubeBottom + yOffset - (tubeWidth * 1.5), (tubeWidth * 0.6), - _value); // body, red

  textSize(32);
  fill(tickColor);
  String label = String.format("%.01f %s", value, unit);
  float strW = textWidth(label);
  text(label, xOffset - (strW / 2), 40 + yOffset);
}
