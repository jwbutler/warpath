package warpath.items;

import warpath.units.BasicUnit;

public class Helmet extends Accessory {
  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;

  public Helmet(BasicUnit unit, String animationName) {
    super(unit, animationName, "head", X_OFFSET, Y_OFFSET);
  }

  // Default sprite
  public Helmet(BasicUnit unit) {
    this(unit, "helmet");
  }
}