import jwbgl.Posn;

public class NonBlockingObject extends BasicObject {
  public NonBlockingObject(RPG game, Posn posn) {
    super(game, posn);
  }

  @Override
  public boolean isObstacle() {
    return false;
  }

}
