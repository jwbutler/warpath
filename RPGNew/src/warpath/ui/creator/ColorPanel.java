package warpath.ui.creator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class ColorPanel extends JPanel implements MouseListener, MouseMotionListener {
  private ColorPicker parent;
  private float hue;
  private float saturation;
  public ColorPanel(ColorPicker parent) {
    super();
    this.parent = parent;
    hue = 0;
    saturation = 0;
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    
    int left = getInsets().left;
    int right = getWidth() - getInsets().right - 1;
    int width = right - left + 1;
    int top = getInsets().top;
    int bottom = getHeight() - getInsets().bottom - 1;
    int height = bottom - top + 1;
    
    Color oldColor = g.getColor();
    for (int y=top; y<=bottom; y++) {
      for (int x=left; x<=right; x++) {
        float hue = ((float)x) / width;
        float saturation = ((float)(height-y)) / height;
        float value = parent.getValue();
        Color c = Color.getHSBColor(hue, saturation, value);
        //System.out.println(String.format("(%d, %d): %s", x,y,c.toString()));
        g.setColor(c);
        g.drawLine(x,y,x,y);
      }
    }
    int x = (int)(hue * width);
    int y = (int)(saturation * height);
    g.setColor(Color.BLACK);
    g.drawRect(x-4, y-4, 8, 8);
    g.setColor(oldColor);
  }
  
  /**
   * TODO: clamp points rather than disallowing
   */
  private void updateColor(MouseEvent e) {
    float newHue, newSat;
    newHue = (float)e.getX()/(getWidth()-1);
    newSat = (float)e.getY()/(getHeight()-1);
    if (newHue >= 0 && newHue <= 1 && newSat >= 0 && newSat <= 1) {
      hue = newHue;
      saturation = newSat;
      repaint();
      parent.repaint();
    }
  }
  
  public void mousePressed(MouseEvent e) {
    updateColor(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    updateColor(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (leftButtonIsDown(e)) updateColor(e);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    updateColor(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void mouseExited(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void mouseReleased(MouseEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  
  private boolean leftButtonIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK);
  }
  
  public float getHue() {
    return hue;
  }
  
  public float getSaturation() {
    return saturation;
  }
}
