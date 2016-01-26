package warpath.ui.creator;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import warpath.layouts.StretchLayout;

public class ColorPicker extends JPanel {
  private final ColorPanel colorPanel;
  private final ValuePanel valuePanel;
  public ColorPicker() {
    setBackground(Color.LIGHT_GRAY);
    setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
    colorPanel = new ColorPanel(this);
    colorPanel.setBackground(Color.WHITE);
    colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    valuePanel = new ValuePanel(this);
    valuePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    setLayout(new StretchLayout(StretchLayout.X_AXIS));
    add(colorPanel, 8.0);
    add(valuePanel, 1.0);
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

}
