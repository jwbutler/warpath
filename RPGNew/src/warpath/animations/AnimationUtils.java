package warpath.animations;

import warpath.core.Constants;
import warpath.core.Direction;
import warpath.core.Utils;

/**
 * Created by Jack on 8/20/2016.
 */
public class AnimationUtils {
  public static String formatFilename(String spriteName, String activity, Direction direction, String animIndex, boolean checkBehind) {
    if (direction == null) {
      String filename = String.format("%s_%s_%s.%s", spriteName, activity, animIndex, Constants.IMAGE_FORMAT);
      if (!checkBehind || Utils.imageExists(filename)) {
        return filename;
      } else {
        return String.format("%s_%s_%s_B.%s", spriteName, activity, animIndex, Constants.IMAGE_FORMAT);
      }
    } else {
      String filename = String.format("%s_%s_%s_%s.%s", spriteName, activity, direction, animIndex, Constants.IMAGE_FORMAT);
      if (!checkBehind || Utils.imageExists(filename)) {
        return filename;
      } else {
        return String.format("%s_%s_%s_%s_B.%s", spriteName, activity, direction, animIndex, Constants.IMAGE_FORMAT);
      }
    }
  }



  /**
   * Load equipment animation filenames dynamically.
   * Specifically, we're doing three things:
   *  - loading it from the correct directory (/png),
   *  - checking for the "_B" suffix, and
   *  - appending the file type (.png) to the end.
   */
  public static String fixAccessoryFilename(String filename) {
    if (Utils.imageExists(filename)) {
      return String.format("%s.%s", filename, Constants.IMAGE_FORMAT);
    } else {
      String behindPath = filename + "_B.%s";
      if (Utils.imageExists(behindPath)) {
        return String.format("%s_B.%s", filename, Constants.IMAGE_FORMAT);
      } else {
        return null;
      }
    }
  }
}
