package warpath.items;

import warpath.units.Unit;

/**
 * Important: this uses "item name" (display name) as its input.
 * Most of these could be replaced by the default constructor, but it's better
 * to maintain clarity.
 */
public class ItemFactory {
  public static Accessory create(Unit unit, String itemName) {
    switch (itemName) {
      case "Shield":
        return new Shield(unit, "shield2");
      case "Sword":
        return new Sword(unit, "sword");
      case "Helmet":
        return new Helmet(unit, "helmet");
      default:
        return null;
    }
  }
}
