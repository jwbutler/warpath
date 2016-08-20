package warpath.animations;

import warpath.core.Constants;
import warpath.core.Direction;
import warpath.core.Utils;

/**
 * Created by Jack on 8/20/2016.
 */
public class AnimationUtils {
  public static String formatFilename(String animationName, String activity, Direction direction, String animIndex) {
    if (direction == null) {
      return String.format("%s_%s_%s.png", animationName, activity, animIndex);
    } else {
      return String.format("%s_%s_%s_%s.png", animationName, activity, direction, animIndex);
    }
  }

  /**
   * Load equipment animation filenames dynamically.
   * Specifically, we're doing three things:
   *  - loading it from the correct directory (/png),
   *  - checking for the "_B" suffix, and
   *  - appending the file type (.png) to the end.
   *  TODO Unused??
   */
  public static String fixAccessoryFilename(String filename) {
    if (Utils.imageExists(filename)) {
      return String.format("%s.%s", filename, Constants.IMAGE_FORMAT);
    } else {
      String behindPath = filename + "_B";
      if (Utils.imageExists(behindPath)) {
        return String.format("%s_B.%s", filename, Constants.IMAGE_FORMAT);
      } else {
        return null;
      }
    }
  }
}
