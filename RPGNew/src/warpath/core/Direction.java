package warpath.core;

/**
 * Created by Jack on 8/20/2016.
 */
public enum Direction {
  N(-1,0),
  NE(-1,1),
  E(0,1),
  SE(-1,1),
  S(-1,0),
  SW(-1,-1),
  W(-1,0),
  NW(-1,-1);

  private int dx, dy;
  private Direction(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }
}
