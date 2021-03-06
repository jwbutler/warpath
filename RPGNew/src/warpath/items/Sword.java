package warpath.items;
import warpath.units.Unit;

public class Sword extends Accessory {

  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -10;

  public Sword(Unit unit, String spriteName) {
    super(unit, spriteName, ItemSlot.MAINHAND, X_OFFSET, Y_OFFSET);
  }

  // Default sprite
  public Sword(Unit unit) {
    this(unit, "sword");
  }
}
