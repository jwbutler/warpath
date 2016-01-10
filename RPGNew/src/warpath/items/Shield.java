package warpath.items;

import warpath.core.RPG;
import warpath.units.Unit;

public class Shield extends Accessory {
  public Shield(RPG game, Unit unit, String animationName) {
    super(game, unit, "shield2", "offhand");
    yOffset = -10;
    xOffset = 0; // just guessing.
  }

}