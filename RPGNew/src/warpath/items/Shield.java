package warpath.items;

import warpath.core.RPG;
import warpath.units.Unit;

public class Shield extends Accessory {
  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;

  public Shield(Unit unit, String animationName) {
    super(unit, animationName, "offhand", X_OFFSET, Y_OFFSET);
  }

  // Default sprite
  public Shield(Unit unit) {
    this(unit, "shield2");
  }

}