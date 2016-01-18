package warpath.animations;
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;

import jwbgl.*;
import warpath.core.Constants;

/** Represents a single animation of a unit.  Corresponds to a particular
 * activity and direction.  It's basically just a collection of Surface
 * objects. */

public class Animation {
  
  private static final String BEHIND_SUFFIX = "_B";
  private final Surface[] frames;
  
  // Used for items to determine whether they render in front of or behind the
  // main unit sprite.
  private final boolean[] drawBehind;
  private String activity;
  private String direction;
  private int index;
  
  public Surface[] getFrames() {
    return frames;
  }

  public String getActivity() {
    return activity;
  }

  public String getDirection() {
    return direction;
  }

  /** Creates a new Animation object.  Before loading a new image file, try
   * to retrieve from the frame cache object to avoid duplication.
   * @param animName - The name of the sprite used.
   * @param filenames - The list of filenames (not including extension or folder)
   * @param direction - The direction of the animation (e.g. "NW")
   * @param frameCache - The parent object's frame cache 
   */
  public Animation(String animName, String[] filenames, String activity, String direction, HashMap<String, Surface> frameCache) {
    frames = new Surface[filenames.length];
    drawBehind = new boolean[filenames.length];
    for (int i = 0; i < filenames.length; i++) {
      String filename = filenames[i];
      drawBehind[i] = (filename.endsWith("_B.png"));
      if (frameCache != null && frameCache.containsKey(filename)) {
        frames[i] = frameCache.get(filename);
      } else {
        frames[i] = new Surface(filename);
        frames[i] = frames[i].scale2x();
        frames[i].setColorkey(Color.WHITE);
        if (frameCache != null) frameCache.put(filename, frames[i]);
      }
    }
    this.activity = activity;
    this.direction = direction;
    index = 0;
  }
  
  /** Instantiate a new Animation without using a frame cache.
   * TODO Is this in use?
   */
  public Animation(String animName, String[] filenames, String activity, String direction) {
    this(animName, filenames, activity, direction, null);
  }
  
  /*public static Animation createFixed(String animName, String[] filenames, String activity, String direction) {
    String[] filenames2 = new String[filenames.length];
    for (int i=0; i<filenames.length; i++) {
      filenames2[i] = fixFilename(animName, filenames[i], direction);
    }
    return new Animation(animName, filenames2, activity, direction);
  }*/

  public int getLength() {
    return frames.length;
  }
  
  public Surface getCurrentFrame() {
    return frames[index];
  }
  
  public void setIndex(int index) {
    this.index = index;
  }
  
  public void nextFrame() {
    index++;
  }
  
  /** Returns true if there are frames after the current frame.
   * @return whether or not there are frames after the current frame
   */
  public boolean hasNextFrame() {
    return (index < frames.length-1);
  }
  
  public int getIndex() {
    return index;
  }
  
  /** For debug purposes; outputs in the form (activity, direction, length)
   */
  public String toString() {
    return "<Animation("+getActivity()+","+getDirection()+","+getLength()+")>";
  }
  
  /**
   * Load equipment animation filenames dynamically.
   * Specifically, we're doing three things:
   *  - loading it from the correct directory (/png),
   *  - checking for the "_B" suffix, and
   *  - appending the file type (.png) to the end.
   */
  public static String fixAccessoryFilename(String filename) {
    String path = String.format("%s%s%s.%s", Constants.IMAGE_FOLDER, File.separator, filename, Constants.IMAGE_FORMAT);

    File f = new File(path);
    if (f.exists()) {
      return String.format("%s.%s", filename, Constants.IMAGE_FORMAT);
    } else {
      String behindPath = String.format("%s%s%s%s.%s", Constants.IMAGE_FOLDER, File.separator, filename, BEHIND_SUFFIX, Constants.IMAGE_FORMAT);
      File fBehind = new File(behindPath);
      if (fBehind.exists()) {
        return String.format("%s%s.%s", filename, BEHIND_SUFFIX, Constants.IMAGE_FORMAT);
      } else {
        return null;
      }
    }
  }
  
  
  public boolean drawBehind(int index) {
    return drawBehind[index];
  }
  
  
}
