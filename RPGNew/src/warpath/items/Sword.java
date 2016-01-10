package warpath.items;
import java.util.ArrayList;

import warpath.animations.Animation;
import warpath.core.RPG;
import warpath.units.Unit;

public class Sword extends Accessory {

  public Sword(RPG game, Unit unit, String animationName) {
    super(game, unit, animationName, "mainhand");
    yOffset = -10;
    xOffset = 0; // just guessing.
  }
}
