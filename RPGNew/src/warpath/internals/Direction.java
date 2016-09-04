package warpath.internals;

import jwbgl.Posn;

import java.util.Arrays;
import java.util.List;

/**
 * @author jbutler
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

  public Direction clockwise() {
    switch(this) {
      case N: return NE;
      case NE: return E;
      case E: return SE;
      case SE: return S;
      case S: return SW;
      case SW: return W;
      case W: return NW;
      case NW: return N;
      default: return NONE;
    }
  }

  public Direction counterClockwise() {
    switch(this) {
      case N: return NW;
      case NE: return N;
      case E: return NE;
      case SE: return E;
      case S: return SE;
      case SW: return S;
      case W: return SW;
      case NW: return W;
      default: return NONE;
    }
  }
}
