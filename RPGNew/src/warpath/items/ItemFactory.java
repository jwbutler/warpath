package warpath.items;

import warpath.core.RPG;
import warpath.units.Unit;

public class ItemFactory {
  public static Accessory create(RPG game, Unit unit, String itemName) {
    switch (itemName) {
    case "shield2":
      return new Shield(game, unit, "shield2");
    case "sword":
      return new Sword(game, unit, "sword");
    default:
      return null;
    }
  }
}
