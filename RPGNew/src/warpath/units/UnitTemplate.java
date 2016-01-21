package warpath.units;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import warpath.items.AccessoryTemplate;

/**
 * Used by the character creator (especially its save/load functionality).
 * Contains the information needed to make a sprite: animation name and
 * palette swaps.
 * TODO think of a more descriptive name that includes equipment
 */
public class UnitTemplate implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String animName;
  private final HashMap<Color, Color> paletteSwaps;
  private final HashMap<String, AccessoryTemplate> equipment;
  
  public UnitTemplate(String animName, HashMap<Color, Color> paletteSwaps) {
    this.animName = animName;
    this.paletteSwaps = paletteSwaps;
    this.equipment = new HashMap<String, AccessoryTemplate>();
  }
  
  public HashMap<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }
  
  public String getAnimName() {
    return animName;
  }
  
  public HashMap<String, AccessoryTemplate> getEquipment() {
    return equipment;
  }
  
  public void addItem(String slot, AccessoryTemplate item) {
    equipment.put(slot, item);
  }
  
}
