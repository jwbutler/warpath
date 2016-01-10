package warpath.ui.components;
import java.awt.Color;
import java.awt.Graphics;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.objects.GameObject;
import warpath.objects.NonBlockingObject;

/** Represents a floor overlay for units with customizable colors 
 * representing the unit's alignment, targeting status, etc. */
public class FloorOverlay extends NonBlockingObject {
  private final String FLOOR_OVERLAY_PATH = "floor_overlay_96x48";
  private GameObject src;
  private Surface surface;
  private Color outerColor;
  private Color innerColor;
  public FloorOverlay(RPG game, GameObject src, Color outerColor, Color innerColor) {
    super(game, src.getPosn());
    this.game = game;
    this.src = src;
    this.outerColor = outerColor;
    this.innerColor = innerColor;
    surface = new Surface(Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    surface.load(String.format("%s.%s", FLOOR_OVERLAY_PATH, Constants.IMAGE_FORMAT));
    
    surface.getPaletteSwaps().put(Color.BLACK, outerColor);
    surface.getPaletteSwaps().put(Color.RED, innerColor);
    surface.applyPaletteSwaps();
    surface.setColorkey(Color.WHITE);
    depthOffset = -1;
    updateDepth();
  }
  
  public FloorOverlay(RPG game, GameObject src, Color color) {
    this(game, src, color, Color.WHITE);
  }
  
  public void draw(Graphics g) {
    Posn pixel = game.gridToPixel(src.getPosn());
    surface.draw(g, pixel);
  }
  
  public GameObject getSource() {
    return src;
  }
  
  public String toString() {
    //return "<FloorOverlay("+outerColor+","+innerColor+")>";
    return String.format("<FloorOverlay(%s, %d)>", getPosn(), this.hashCode());
  }
}