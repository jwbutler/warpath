package warpath.ui;

import java.awt.Color;
import java.util.HashMap;

public class BaseColors {
  public static HashMap<String, Color> get(String animName) {
    HashMap<String, Color> baseColors = new HashMap<String, Color>();
    switch (animName) {
    case "player":
    case "female":
      baseColors.put("Hair", new Color(128,64,0));
      baseColors.put("Face", new Color(255,128,64));
      baseColors.put("Eyes", new Color(0,64,64));
      baseColors.put("Mouth", new Color(128,0,0));
      baseColors.put("Shirt 1", new Color(128,0,128));
      baseColors.put("Shirt 2", new Color(255,0,255));
      baseColors.put("Shirt 3", new Color(0,0,128));
      baseColors.put("Hands", new Color(0,255,255));
      baseColors.put("Belt", new Color(0,0,0));
      baseColors.put("Skirt", new Color(128,128,128));
      baseColors.put("Legs", new Color(192,192,192));
      baseColors.put("Boots 1", new Color(0,128,0));
      baseColors.put("Boots 2", new Color(0,255,0));
      return baseColors;
    case "sword":
    case "shield2":
      baseColors.put("Color 1", new Color(0,0,0));
      baseColors.put("Color 2", new Color(128,128,128));
      baseColors.put("Color 1", new Color(192,192,192));
      return baseColors;
    default:
      return baseColors;
    }
  }
  
  /**
   * TODO validation
   */
  public static HashMap<Color, Color> getDefaultPaletteSwaps(String animName) {
    HashMap<String, Color> baseColors = BaseColors.get(animName);
    HashMap<Color, Color> paletteSwaps = new HashMap<Color, Color>();
    for (String s : baseColors.keySet()) {
      Color c = baseColors.get(s);
      paletteSwaps.put(c,c);
    }
    return paletteSwaps;
    
  }
}
