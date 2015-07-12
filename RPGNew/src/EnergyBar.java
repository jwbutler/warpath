import java.awt.Color;
import java.awt.Graphics;
import jwbgl.*;

/* This is just a copy/paste of HealthBar with different colors.
 * Some time in the future we might want to make a superclass. */

public class EnergyBar extends Surface {
  protected Unit unit;
  public EnergyBar(Unit unit, int width, int height) {
    super(width, height);
    this.unit = unit;
  }
  
  @Override
  public void draw(Graphics g, int x, int y, float alpha) {
    redraw();
    super.draw(g, x, y, alpha);
  }
  
  public void redraw() {
    Graphics g = getGraphics();
    g.setColor(Color.WHITE);
    g.drawRect(0, 0, getWidth(), getHeight());
    
    g.setColor(Color.RED);
    g.fillRect(1, 1, getWidth()-2, getHeight()-2);
    double epRatio = (double) (unit.getCurrentEP())/ ((double) unit.getMaxEP());
    int yellowWidth = (int) (epRatio*(getWidth()-2));
    
    g.setColor(Color.YELLOW); 
    g.fillRect(1, 1, yellowWidth, getHeight()-2);
  }
  
}
