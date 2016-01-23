package warpath.templates;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class TemplateFactory {
  public static SpriteTemplate getTemplate(String animName) {
    ArrayList<String> colorList = new ArrayList<String>();
    HashMap<String, Color> colorMap = new HashMap<String, Color>();
    if (animName.equals("player") || animName.equals("female")) {
      String[] colorNames = {
        "Hair", "Face", "Eyes", "Mouth", "Shirt 1", "Shirt 2", "Shirt 3",
        "Hands", "Belt", "Skirt", "Legs", "Boots 1", "Boots 2"
      };
      for (String c : colorNames) colorList.add(c);
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
      HashMap<Color, Color> paletteSwaps = getDefaultPaletteSwaps(colorMap);
      return new UnitTemplate(animName, colorList, colorMap, paletteSwaps);
    } else if (animName.equals("zombie")) {
      String[] colorNames = { "Hair/Feet", "Eyes", "Skin 1", "Skin 2", "Shirt", "Pants", "Blood" };
      colorMap.put("Hair/Feet", new Color(128, 64, 0));
      colorMap.put("Eyes", new Color(0, 128, 128));
      colorMap.put("Skin 1", new Color(186, 183, 120));
      colorMap.put("Skin 2", new Color(128, 128, 64));
      colorMap.put("Shirt", new Color(128, 128, 128));
      colorMap.put("Pants", new Color(0, 0, 0));
      colorMap.put("Blood", new Color(128, 0, 0));
      HashMap<Color, Color> paletteSwaps = getDefaultPaletteSwaps(colorMap);
      return new UnitTemplate(animName, colorList, colorMap, paletteSwaps);
    } else if (animName.equals("sword") || animName.equals("shield2")) {
      String[] colorNames = { "Color 1", "Color 2", "Color 3" };
      for (String c : colorNames) colorList.add(c);
      colorMap.put("Color 1", new Color(0,0,0));
      colorMap.put("Color 2", new Color(128,128,128));
      colorMap.put("Color 3", new Color(192,192,192));
      HashMap<Color, Color> paletteSwaps = getDefaultPaletteSwaps(colorMap);
      return new AccessoryTemplate(animName, colorList, colorMap, paletteSwaps);
    } else {
      return null;
    }
  }
  
  /**
   * TODO validation
   * can this be private?
   */
  public static HashMap<Color, Color> getDefaultPaletteSwaps(HashMap<String, Color> baseColors) {
    HashMap<Color, Color> paletteSwaps = new HashMap<Color, Color>();
    for (String s : baseColors.keySet()) {
      Color c = baseColors.get(s);
      paletteSwaps.put(c,c);
    }
    return paletteSwaps;
  }
}
