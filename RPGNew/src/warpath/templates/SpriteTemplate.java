package warpath.templates;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Contains info needed to save any created object (unit or accessory).
 * Extended by UnitTemplate, AccessoryTemplate.
 */
public class SpriteTemplate implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String spriteName;
  private final List<String> colorList;
  private final Map<String, Color> colorMap;
  private final Map<Color, Color> paletteSwaps;
  
  public SpriteTemplate(String spriteName, List<String> colorList, Map<String, Color> colorMap, Map<Color, Color> paletteSwaps) {
    this.spriteName = spriteName;
    this.colorList = colorList;
    this.colorMap = colorMap;
    this.paletteSwaps = paletteSwaps;
  }
  
  public List<String> getColorList() {
    return colorList;
  }
  
  public Map<String,Color> getColorMap() {
    return colorMap;
  }
  
  public Map<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }
  
  public String getSpriteName() {
    return spriteName;
  }
}