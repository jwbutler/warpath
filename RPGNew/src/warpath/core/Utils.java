package warpath.core;

import java.io.File;

import jwbgl.Posn;
import warpath.objects.GameObject;

public class Utils {

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
  
  public static boolean isSaveFile(String filename) {
    return filename.endsWith("."+Constants.CHARACTER_SAVE_FORMAT);
  }
  
  public static boolean imageExists(String filename) {
    String fullFilename = Constants.IMAGE_FOLDER + File.separator + filename;
    return (new File(fullFilename).exists());
  }

}
