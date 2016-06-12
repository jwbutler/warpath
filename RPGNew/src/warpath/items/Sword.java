package warpath.items;
import warpath.core.RPG;
import warpath.units.Unit;

public class Sword extends Accessory {

  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;
  public Sword(RPG game, Unit unit, String animationName) {
    super(game, unit, animationName, "mainhand", X_OFFSET, Y_OFFSET);
  }
  public Sword(RPG game, Unit unit) {
    this(game, unit, "sword");
  }
}
