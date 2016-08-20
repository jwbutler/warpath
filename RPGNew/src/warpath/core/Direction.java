package warpath.core;

import jwbgl.Posn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jack on 8/20/2016.
 */
public enum Direction {
  N(0,-1),
  NE(1,-1),
  E(1,0),
  SE(1,1),
  S(0,1),
  SW(-1,1),
  W(-1,0),
  NW(-1,-1),
  NONE(0,0);

  private final static List<Direction> DIRECTIONS = Arrays.asList(
    N,NE,E,SE,S,SW,W,NW
  );

  public int dx, dy;
  private Direction(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  public Posn toPosn() {
    return new Posn(dx, dy);
  }

  public static List<Direction> directions() {
    return DIRECTIONS;
  }

  public static Direction from(int dx, int dy) {
    for (Direction dir : DIRECTIONS) {
      if (dir.dx == dx && dir.dy == dy) {
        return dir;
      }
    }
    return NONE;
  }
}
