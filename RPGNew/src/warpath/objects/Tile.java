package warpath.objects;
import java.awt.Color;
import java.util.ArrayList;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.units.Unit;
 
/** Represents a floor tile.  Contains a list of objects that live on it, as
 * well as its current unit. */
public class Tile extends BasicObject {
  private Surface surface;
  private Unit unit;
  private final ArrayList<GameObject> objects;
  public Tile(RPG game, Posn posn, String texture) {
    super(game, posn);
    surface = new Surface(Constants.TILE_WIDTH/2, Constants.TILE_HEIGHT/2);
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
  
  /**
   * Returns true if the tile contains a unit or other blocking object.
   */
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
  
  /**
   * Sets the unit pointer to u.
   * TODO Throw an exception, or maybe make it a boolean?
   * @param u
   */
  public void setUnit(Unit u) {
    if (unit != null && u != null) {
      System.out.println("Error: tile already has unit");
    }
    unit = u;
  }
  
  public String toString() {
    return String.format("Tile<%s, %s>", getPosn(), unit);
  }
}
