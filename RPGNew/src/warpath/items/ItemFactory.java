package warpath.items;

import warpath.core.RPG;
import warpath.units.Unit;

/**
 * Important: this uses "item name" (display name) as its input.
 * Most of these could be replaced by the default constructor, but it's better
 * to maintain clarity.
 */
public class ItemFactory {
  public static Accessory create(RPG game, Unit unit, String itemName) {
    switch (itemName) {
      case "Shield":
        return new Shield(game, unit, "shield2");
      case "Sword":
        return new Sword(game, unit, "sword");
      case "Helmet":
        return new Helmet(game, unit, "helmet");
      default:
        return null;
    }
  }
}
