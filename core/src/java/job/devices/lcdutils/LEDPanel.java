package job.devices.lcdutils;

import java.awt.Color;
import java.awt.Graphics;

public class LEDPanel
  extends javax.swing.JPanel {
  private Color ledColor = Color.red;
  private boolean withGrid = false;

  private static int NB_LINES =  32;
  private static int NB_COLS  = 128;

  private int nbLines, nbCols;

  private boolean[][] ledOnOff;

  public void setLedOnOff(boolean[][] ledOnOff) {
    this.ledOnOff = ledOnOff;
  }

  public boolean[][] getLedOnOff() {
    return ledOnOff;
  }

  /** Creates new form LEDPanel */
  public LEDPanel()
  {
    this(NB_LINES, NB_COLS);
  }

  public LEDPanel(int nbLines, int nbCols) {
    this.nbLines = nbLines;
    this.nbCols = nbCols;
    initLeds();
    initComponents();
  }

  public void setWithGrid(boolean withGrid) {
    this.withGrid = withGrid;
  }

  private void initLeds() {
    this.ledOnOff = new boolean[this.nbLines][this.nbCols];
    for (int r=0; r<this.nbLines; r++) {
      for (int c=0; c<this.nbCols; c++) {
        this.ledOnOff[r][c] = false;
      }
    }
  }

  public void clear() {
    initLeds();
    this.repaint();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   */
  private void initComponents() {
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );
  }

  public void setImage(boolean[][] leds) {
    if (leds.length != this.nbLines) {
      System.out.println("Width mismatch " + leds.length + " instead of " + this.nbLines);
      return;
    }
    if (leds[0].length != this.nbCols) {
      System.out.println("Height mismatch " + leds[0].length + " instead of " + this.nbCols);
      return;
    }
    for (int r=0; r<this.nbLines; r++) {
      for (int c=0; c<this.nbCols; c++) {
        ledOnOff[r][c] = leds[r][c];
      }
    }
    this.repaint();
  }

  @Override
  public void paintComponent(Graphics gr) {
    gr.setColor(Color.black);
    gr.fillRect(0, 0, this.getWidth(), this.getHeight());
    // Grid
    if (withGrid) {
      gr.setColor(Color.gray);
      for (int c=0; c<this.nbCols; c++) {
	      gr.drawLine(c * this.getWidth() / this.nbCols, 0, c * this.getWidth() / this.nbCols, this.getHeight());
      }
      for (int r=0; r<this.nbLines; r++) {
	      gr.drawLine(0, r * this.getHeight() / this.nbLines, this.getWidth(), r * this.getHeight() / this.nbLines);
      }
    }
    gr.setColor(ledColor);
    for (int r=0; r<this.nbLines; r++) {
      for (int c=0; c<this.nbCols; c++) {
        if (ledOnOff[r][c]) {
          // Change that, with gradient and this sort of stuff, if necessary
          gr.fillRoundRect(c * this.getWidth() / this.nbCols,
                           r * this.getHeight() / this.nbLines,
                           this.getWidth() / this.nbCols,
                           this.getHeight() / this.nbLines,
                           20,
                           20);
        }
      }
    }
  }
}
