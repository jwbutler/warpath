package warpath.core;

import java.io.File;

import jwbgl.Posn;
import warpath.objects.GameObject;

public class Utils {

  /**
   * Calculate the distance between two points.
   * IMPORTANT: not Pythagorean distance, but Civ-style (I think) distance.
   * Returns the larger of the x-distance and the y-distance.
   * @param p the first point
   * @param q the second point
   */
  public static int distance(Posn p, Posn q) {
    int dx = Math.abs(q.getX() - p.getX());
    int dy = Math.abs(q.getY() - p.getY());
    return Math.max(dx, dy);
  }

  public static int distance(GameObject x, GameObject y) {
    return distance(x.getPosn(), y.getPosn());
  }
  
  public static boolean isSaveFile(String filename) {
    return filename.endsWith(String.format(".%s", Constants.CHARACTER_SAVE_FORMAT));
  }
  
  public static boolean imageExists(String filename) {
    String fullFilename = Constants.IMAGE_FOLDER + File.separator + filename;
    return (new File(fullFilename).exists());
  }

}
