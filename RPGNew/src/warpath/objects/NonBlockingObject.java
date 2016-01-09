package warpath.objects;
import jwbgl.Posn;
import warpath.core.RPG;
import warpath.units.BasicObject;

public class NonBlockingObject extends BasicObject {
  public NonBlockingObject(RPG game, Posn posn) {
    super(game, posn);
  }

  @Override
  public boolean isObstacle() {
    return false;
  }

}
