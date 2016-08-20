package warpath.templates;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AccessoryTemplate extends SpriteTemplate implements Serializable {
  private static final long serialVersionUID = 1L;

  public AccessoryTemplate(String animName, List<String> colorList, Map<String, Color> colorMap, Map<Color, Color> paletteSwaps) {
    super(animName, colorList, colorMap, paletteSwaps);
  }
}