package warpath.items;

import warpath.core.RPG;
import warpath.units.Unit;

public class Shield extends Accessory {
  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;
  public Shield(RPG game, Unit unit, String animationName) {
    super(game, unit, "shield2", "offhand", X_OFFSET, Y_OFFSET);
  }

}