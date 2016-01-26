package warpath.ui.creator;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import warpath.layouts.StretchLayout;

public class ColorPicker extends JPanel {
  private static final long serialVersionUID = 1L;
  private final ColorPanel colorPanel;
  private final ValuePanel valuePanel;
  private final CharacterCreator cc;
  public ColorPicker(CharacterCreator cc) {
    setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
    colorPanel = new ColorPanel(this);
    colorPanel.setBackground(Color.WHITE);
    colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    valuePanel = new ValuePanel(this);
    valuePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    setLayout(new StretchLayout(StretchLayout.X_AXIS));
    add(colorPanel, 7.0);
    add(valuePanel, 1.0);
    this.cc = cc;
    //validate();
  }
  
  public float getValue() {
    return valuePanel.getValue();
  }
  
  public float getHue() {
    return colorPanel.getHue();
  }
  
  public float getSaturation() {
    return colorPanel.getSaturation();
  }
  
  public Color getColor() {
    return Color.getHSBColor(getHue(), getSaturation(), getValue());
  }
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    int offset = 4; 
    int x = (int) valuePanel.getBounds().getMaxX() - 1 + offset;
    int y = (int) (valuePanel.getBounds().getMinY() + (valuePanel.getBounds().getHeight()-1)*(1-getValue()));
    Color oldColor = g.getColor();
    g.setColor(Color.BLACK);
    // Make a triangle.
    int size = 10;
    g.fillPolygon(
      new int[]{x, x+size, x+size},
      new int[]{y, y-size, y+size},
      3
    );
    g.setColor(oldColor);
  }

  /**
   * Called by both child panels when the selected color is changed, as part
   * of their mouse listeners.  Call repaint(), and tell the game to update
   * its color.
   */
  public void updateColor() {
    repaint();
    cc.updateColors();
  }
  
  public void setColor(Color c) {
    colorPanel.setColor(c);
    valuePanel.setColor(c);
  }

}
