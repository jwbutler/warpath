package warpath.items;

import warpath.units.Unit;

public class Helmet extends Accessory {
  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;

  public Helmet(Unit unit, String animationName) {
    super(unit, animationName, "head", X_OFFSET, Y_OFFSET);
  }

  // Default sprite
  public Helmet(Unit unit) {
    this(unit, "helmet");
  }
}