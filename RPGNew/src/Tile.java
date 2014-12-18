import java.awt.Color;
import java.util.ArrayList;

/* Used to represent a single floor tile.  Contains a surface, as well as a
 * unit reference and a list of other objects that are on it. */
 
 /* ===== CHANGELOG =====
  * 5/26 - Changed from ArrayList of units to single unit.
  * ===================== */

public class Tile extends BasicObject {
  private Surface surface;
  private Unit unit;
  // let's say objects DOESN'T include units
  // corpses are unclear
  private ArrayList<GameObject> objects;
  public Tile(RPG game, Posn posn, String texture) {
    super(game, posn);
    surface = new Surface(RPG.TILE_WIDTH/2, RPG.TILE_HEIGHT/2);
    surface.load(texture);
    surface = surface.scale2x();
    surface.setColorkey(Color.WHITE); 
    unit = null;
    objects = new ArrayList<GameObject>();
  }
  public Surface getSurface() {
    return surface;
  }
  
  public Unit getUnit() {
    return unit;
  }
  
  public ArrayList<GameObject> getObjects() {
    return objects;
  }
  
  public boolean isBlocked() {
    if (unit != null) {
      return true;
    } else {
      for (int i = 0; i < objects.size(); i++) {
        if (objects.get(i).isObstacle()) {
          return true;
        }
      }
    }
    return false;
  }
  public void setUnit(Unit u) {
    if (unit != null && u != null) {
      System.out.println("Error: tile already has unit");
    }
    unit = u;
  }
}
