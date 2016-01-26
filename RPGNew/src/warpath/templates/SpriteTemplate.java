package warpath.templates;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains info needed to save any created object (unit or accessory).
 * Extended by UnitTemplate, AccessoryTemplate.
 */
public class SpriteTemplate implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String animName;
  private final ArrayList<String> colorList;
  private final HashMap<String, Color> colorMap;
  private final HashMap<Color, Color> paletteSwaps;
  
  public SpriteTemplate(String animName, ArrayList<String> colorList, HashMap<String, Color> colorMap, HashMap<Color, Color> paletteSwaps) {
    this.animName = animName;
    this.colorList = colorList;
    this.colorMap = colorMap;
    this.paletteSwaps = paletteSwaps;
  }
  
  public ArrayList<String> getColorList() {
    return colorList;
  }
  
  public HashMap<String,Color> getColorMap() {
    return colorMap;
  }
  
  public HashMap<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }
  
  public String getAnimName() {
    return animName;
  }
}