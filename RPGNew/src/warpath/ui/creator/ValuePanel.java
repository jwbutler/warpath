package warpath.ui.creator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class ValuePanel extends JPanel implements MouseListener, MouseMotionListener {
  private float value;
  private ColorPicker parent;
  public ValuePanel(ColorPicker parent) {
    super();
    this.parent = parent;
    value = 1;
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    
    int left = getInsets().left;
    int right = getWidth() - getInsets().right - 1;
    int top = getInsets().top;
    int bottom = getHeight() - getInsets().bottom - 1;
    int height = bottom - top + 1;
    Color oldColor = g.getColor();
    for (int y=top; y<=bottom; y++) {
      // System.out.println(String.format("(%d, %d): %s", x,y,c.toString()));
      Color c = Color.getHSBColor(
        parent.getHue(),
        parent.getSaturation(),
        1 - ((float)y / height)
      );
      g.setColor(c);
      g.drawLine(left,y,right,y);
    }
    
    /*
    int y = (int)((1-value)*(getHeight()-1));
    g.setColor(Color.BLUE);
    g.fillRect(0,y-8,getWidth(),15);
    */
    g.setColor(oldColor);
  }
  
  private void updateValue(MouseEvent e) {
    float newValue = 1 - (((float)e.getY())/(getHeight()-1));
    if (newValue >= 0 && newValue <= 1) {
      value = newValue; 
      repaint();
      parent.updateColor();
    }
  }
  
  public float getValue() { return value; }
  
  public void mousePressed(MouseEvent e) {
    updateValue(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    updateValue(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (leftButtonIsDown(e)) updateValue(e);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    updateValue(e);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub
  }
  
  private boolean leftButtonIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK);
  }
}