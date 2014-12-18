import java.awt.Graphics;

public class TransHealthBar extends HealthBar {
  public TransHealthBar(Unit unit, int width, int height) {
    super(unit, width, height);
  }
  public void draw(Graphics g, int width, int height) {
    super.draw(g, width, height, (float)0.5);
  }
}
