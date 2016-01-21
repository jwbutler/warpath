package warpath.items;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import warpath.internals.SpriteTemplate;

public class AccessoryTemplate extends SpriteTemplate implements Serializable {
  private static final long serialVersionUID = 1L;

  public AccessoryTemplate(String animName, ArrayList<String> colorList, HashMap<String, Color> colorMap, HashMap<Color, Color> paletteSwaps) {
    super(animName, colorList, colorMap, paletteSwaps);
  }
}