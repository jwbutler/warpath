package warpath.animations;
import java.awt.Color;
import java.util.HashMap;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.Utils;

/**
 * Represents a single animation of a unit.  Corresponds to a particular
 * activity and direction.  It's basically just a collection of Surface
 * objects.
 */

public class Animation {
  
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

  /**
   * Creates a new Animation object.  Before loading a new image file, try
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
  
  
  public boolean drawBehind(int index) {
    return drawBehind[index];
  }
  
  
}
