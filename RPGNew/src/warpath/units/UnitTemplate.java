package warpath.units;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import warpath.internals.SpriteTemplate;
import warpath.items.AccessoryTemplate;

/**
 * Used by the character creator (especially its save/load functionality).
 * Contains the information needed to make a sprite: animation name and
 * palette swaps.
 * TODO think of a more descriptive name that includes equipment
 */
public class UnitTemplate extends SpriteTemplate implements Serializable {
  private static final long serialVersionUID = 2L;
  private final HashMap<String, AccessoryTemplate> equipment;
  
  public UnitTemplate(String animName, ArrayList<String> colorList, HashMap<String, Color> colorMap, HashMap<Color, Color> paletteSwaps) {
    super(animName, colorList, colorMap, paletteSwaps);
    this.equipment = new HashMap<String, AccessoryTemplate>();
  }
  
  public HashMap<String, AccessoryTemplate> getEquipment() {
    return equipment;
  }
  
  /**
   * NB: overwites existing items
   */
  public void addItem(String slot, AccessoryTemplate item) {
    equipment.put(slot, item);
  }
  
}
