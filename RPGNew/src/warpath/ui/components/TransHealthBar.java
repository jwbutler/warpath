package warpath.ui.components;
import java.awt.Graphics;

import warpath.units.BasicUnit;

public class TransHealthBar extends HealthBar {
  public TransHealthBar(BasicUnit unit, int width, int height) {
    super(unit, width, height);
  }
  public void draw(Graphics g, int width, int height) {
    super.draw(g, width, height, (float)0.5);
  }
}
