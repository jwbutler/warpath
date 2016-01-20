package warpath.core;

import jwbgl.Posn;
import warpath.objects.GameObject;

public class Utils {

  /**
   * Given an (x,y) pair, returns the corresponding compass direction.
   */ 
  public static String coordsToDir(int x, int y) {
    String rtn = "";
    if (y == -1) {
      rtn = "N";
    } else if (y == 1) {
      rtn = "S";
    }
    if (x == -1) {
      rtn += "W";
    } else if (x == 1) {
      rtn+= "E";
    }
    if (rtn.equals("")) {
      return null; 
    } else {
      return rtn;
    }
  }

  /**
   * Given an (x,y) pair, returns the corresponding compass direction.
   */ 
  public static String coordsToDir(Posn posn) {
    return coordsToDir(posn.getX(), posn.getY());
  }

  /** Calculate the distance between two points.
   * IMPORTANT: not Pythagorean distance, but Civ-style (I think) distance.
   * Returns the larger of the x-distance and the y-distance.
   * @param p the first point
   * @param q the second point
   */
  public static int distance2(Posn p, Posn q) {
    int dx = Math.abs(q.getX() - p.getX());
    int dy = Math.abs(q.getY() - p.getY());
    return Math.max(dx, dy);
  }

  public static int distance2(GameObject x, GameObject y) {
    return distance2(x.getPosn(), y.getPosn());
  }

  public static double distance(Posn p, Posn q) { 
    int dx = Math.abs(q.getX() - p.getX());
    int dy = Math.abs(q.getY() - p.getY());
    return Math.sqrt(dx*dx + dy*dy);
  }

  public static double distance(GameObject p, GameObject q) { 
    double dx = Math.abs(q.getX() - p.getX());
    double dy = Math.abs(q.getY() - p.getY());
    return Math.sqrt(dx*dx + dy*dy);
  }
  
  public static boolean isSaveFile(String s) {
    return s.endsWith("."+Constants.CHARACTER_SAVE_FORMAT);
  }

}
