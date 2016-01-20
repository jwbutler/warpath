package warpath.units;

import java.awt.Color;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Used by the character creator (especially its save/load functionality).
 * Contains the information needed to make a sprite: animation name and
 * palette swaps.
 */
public class UnitTemplate implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String animName;
  private final HashMap<Color, Color> paletteSwaps;
  public UnitTemplate(String animName, HashMap<Color, Color> paletteSwaps) {
    this.animName = animName;
    this.paletteSwaps = paletteSwaps;
  }
  public HashMap<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }
  
}
