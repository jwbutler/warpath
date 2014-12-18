import java.awt.Color;
import java.awt.Graphics;
// Represents a glowy spot on the floor.
// Can be tied either to a Tile or to an object/unit.


public class FloorOverlay extends BasicObject {
  private GameObject src;
  private RPG game;
  private Surface surface;
  private Color outerColor;
  private Color innerColor;
  public FloorOverlay(RPG game, GameObject src, Color outerColor, Color innerColor) {
    super(game, src.getPosn());
    this.game = game;
    this.src = src;
    this.outerColor = outerColor;
    this.innerColor = innerColor;
    surface = new Surface(RPG.TILE_WIDTH, RPG.TILE_HEIGHT);
    surface.load("floor_overlay_96x48.png");
    
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
    return "<FloorOverlay("+outerColor+","+innerColor+")>";
  }
}
