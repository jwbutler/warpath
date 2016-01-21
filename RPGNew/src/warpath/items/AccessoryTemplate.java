package warpath.items;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

/**
 * TODO make a superclass
 */

public class AccessoryTemplate implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String animName;
  private final HashMap<Color, Color> paletteSwaps;
  
  public AccessoryTemplate(String animName, HashMap<Color, Color> paletteSwaps) {
    this.animName = animName;
    this.paletteSwaps = paletteSwaps;
  }
  
  public HashMap<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }
  
  public String getAnimName() {
    return animName;
  }
}