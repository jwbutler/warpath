package warpath.ui.components;
import java.awt.Color;
import java.awt.Graphics;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.objects.GameObject;
import warpath.objects.NonBlockingObject;

/**
 * Represents a floor overlay for units with customizable colors 
 * representing the unit's alignment, targeting status, etc.
 */
public class FloorOverlay extends NonBlockingObject {
  private final String FLOOR_OVERLAY_PATH = "floor_overlay_96x48";
  private GameObject src;
  private Surface surface;
  private final Color outerColor;
  private final Color innerColor;
  private final int X_OFFSET = 0;
  private final int Y_OFFSET = 0;
  private final int DEPTH_OFFSET = -1;
  public FloorOverlay(GameObject src, Color outerColor, Color innerColor) {
    super(src.getPosn());
    setXOffset(X_OFFSET);
    setYOffset(Y_OFFSET);
    setDepthOffset(DEPTH_OFFSET);
    this.src = src;
    this.outerColor = outerColor;
    this.innerColor = innerColor;
    surface = new Surface(Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    surface.load(String.format("%s.%s", FLOOR_OVERLAY_PATH, Constants.IMAGE_FORMAT));
    
    surface.getPaletteSwaps().put(Color.BLACK, outerColor);
    surface.getPaletteSwaps().put(Color.RED, innerColor);
    surface.applyPaletteSwaps();
    surface.setColorkey(Color.WHITE);
    updateDepth();
  }
  
  public FloorOverlay(GameObject src, Color color) {
    this(src, color, Color.WHITE);
  }
  
  public void draw(Graphics g) {
    RPG game = RPG.getInstance();
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
