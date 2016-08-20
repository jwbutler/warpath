package warpath.items;
import warpath.units.BasicUnit;

public class Sword extends Accessory {

  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;

  public Sword(BasicUnit unit, String animationName) {
    super(unit, animationName, "mainhand", X_OFFSET, Y_OFFSET);
  }

  // Default sprite
  public Sword(BasicUnit unit) {
    this(unit, "sword");
  }
}
