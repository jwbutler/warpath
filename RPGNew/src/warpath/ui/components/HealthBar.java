package warpath.ui.components;
import java.awt.Color;
import java.awt.Graphics;
import jwbgl.*;
import warpath.units.Unit;

public class HealthBar extends Surface {
  protected final Unit unit;
  public HealthBar(Unit unit, int width, int height) {
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
    double hpRatio = (double) (unit.getCurrentHP())/ ((double) unit.getMaxHP());
    int greenWidth = (int) (hpRatio*(getWidth()-2));
    
    g.setColor(Color.GREEN); 
    g.fillRect(1, 1, greenWidth, getHeight()-2);
  }
  
}
