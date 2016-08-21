package warpath.templates;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used by the character creator (especially its save/load functionality).
 * Contains the information needed to make a sprite: animation name and
 * palette swaps.
 */
public class UnitTemplate extends SpriteTemplate implements Serializable {
  private static final long serialVersionUID = 2L;
  private final HashMap<String, AccessoryTemplate> equipment;
  
  public UnitTemplate(String spriteName, List<String> colorList, Map<String, Color> colorMap, Map<Color, Color> paletteSwaps) {
    super(spriteName, colorList, colorMap, paletteSwaps);
    this.equipment = new HashMap<>();
  }
  
  /**
   * NB: overwites existing items
   */
  public void addItem(String slot, AccessoryTemplate item) {
    equipment.put(slot, item);
  }

  public void removeItem(String slot) {
    equipment.remove(slot);
  }
  
  public AccessoryTemplate getItem(String slot) {
    return equipment.get(slot);
  }

  public HashMap<String, AccessoryTemplate> getEquipment() {
    return equipment;
  }
  
}
