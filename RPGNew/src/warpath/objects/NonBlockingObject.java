package warpath.objects;
import jwbgl.Posn;
import warpath.core.RPG;

/**
 * Simply represents an object that does not block unit movement,
 * e.g. a corpse.
 **/
public abstract class NonBlockingObject extends BasicObject {

  public NonBlockingObject(Posn posn) {
    super(posn);
  }

  @Override
  public boolean isObstacle() {
    return false;
  }

}
