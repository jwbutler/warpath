package warpath.templates;

import java.awt.Color;
import java.util.*;

public class TemplateFactory {
  public static SpriteTemplate getTemplate(String spriteName) {
    new ArrayList<>();
    Map<String, Color> colorMap = new HashMap<>();
    // TODO Enum!

    if (spriteName.equals("player") || spriteName.equals("female")) {
      List<String> colorList = Arrays.asList(
        "Hair", "Face", "Eyes", "Mouth", "Shirt 1", "Shirt 2", "Shirt 3",
        "Hands", "Belt", "Skirt", "Legs", "Boots 1", "Boots 2"
      );
      colorMap.put("Hair", new Color(128,64,0));
      colorMap.put("Face", new Color(255,128,64));
      colorMap.put("Eyes", new Color(0,64,64));
      colorMap.put("Mouth", new Color(128,0,0));
      colorMap.put("Shirt 1", new Color(128,0,128));
      colorMap.put("Shirt 2", new Color(255,0,255));
      colorMap.put("Shirt 3", new Color(0,0,128));
      colorMap.put("Hands", new Color(0,255,255));
      colorMap.put("Belt", new Color(0,0,0));
      colorMap.put("Skirt", new Color(128,128,128));
      colorMap.put("Legs", new Color(192,192,192));
      colorMap.put("Boots 1", new Color(0,128,0));
      colorMap.put("Boots 2", new Color(0,255,0));
      Map<Color, Color> paletteSwaps = getDefaultPaletteSwaps(colorMap);
      return new UnitTemplate(spriteName, colorList, colorMap, paletteSwaps);
    } else if (spriteName.equals("zombie")) {
      List<String> colorList = Arrays.asList("Hair/Feet", "Eyes", "Skin 1", "Skin 2", "Shirt", "Pants", "Blood");
      colorMap.put("Hair/Feet", new Color(128, 64, 0));
      colorMap.put("Eyes", new Color(0, 128, 128));
      colorMap.put("Skin 1", new Color(186, 183, 120));
      colorMap.put("Skin 2", new Color(128, 128, 64));
      colorMap.put("Shirt", new Color(128, 128, 128));
      colorMap.put("Pants", new Color(0, 0, 0));
      colorMap.put("Blood", new Color(128, 0, 0));
      Map<Color, Color> paletteSwaps = getDefaultPaletteSwaps(colorMap);
      return new UnitTemplate(spriteName, colorList, colorMap, paletteSwaps);
    } else if (spriteName.equals("sword") || spriteName.equals("shield2")) {
      List<String> colorList = Arrays.asList("Color 1", "Color 2", "Color 3");
      colorMap.put("Color 1", new Color(0,0,0));
      colorMap.put("Color 2", new Color(128,128,128));
      colorMap.put("Color 3", new Color(192,192,192));
      Map<Color, Color> paletteSwaps = getDefaultPaletteSwaps(colorMap);
      return new AccessoryTemplate(spriteName, colorList, colorMap, paletteSwaps);
    } else {
      return null;
    }
  }
  
  /**
   * TODO validation
   * can this be private?
   */
  public static Map<Color, Color> getDefaultPaletteSwaps(Map<String, Color> baseColors) {
    Map<Color, Color> paletteSwaps = new HashMap<>();
    for (String s : baseColors.keySet()) {
      Color c = baseColors.get(s);
      paletteSwaps.put(c,c);
    }
    return paletteSwaps;
  }
}
