package warpath.animations;
import java.awt.Color;
import java.io.File;
import java.util.Hashtable;

import jwbgl.*;

/* Represents a single animation of a unit.  Corresponds to a particular
 * activity and direction.  It's basically just a collection of Surface
 * objects. */

public class Animation {
  private Surface[] frames;
  private boolean[] drawBehind;
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

  public Animation(String animName, String[] filenames, String activity, String direction, Hashtable<String, Surface> ht) {
    frames = new Surface[filenames.length];
    drawBehind = new boolean[filenames.length];
    for (int i = 0; i < filenames.length; i++) {
      String filename = filenames[i];
      drawBehind[i] = (filename.endsWith("_B.png"));
      if (ht != null && ht.containsKey(filename)) {
        frames[i] = ht.get(filename);
      } else {
        frames[i] = new Surface(filename);
        frames[i] = frames[i].scale2x();
        frames[i].setColorkey(Color.WHITE);
        if (ht != null) ht.put(filename, frames[i]);
      }
    }
    this.activity = activity;
    this.direction = direction;
    index = 0;
  }
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
  
  public Animation(Surface[] frames, String activity, String direction) {
    this.frames = frames;
    this.activity = activity;
    this.direction = direction;
    index = 0;
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
  
  public boolean hasNextFrame() {
    return index < frames.length-1;
  }
  
  public int getIndex() {
    return index;
  }
  
  public String toString() {
    return "<Animation("+getActivity()+","+getDirection()+","+getLength()+")>";
  }
  
  /* NEW: From now on we're going to format filenames ourselves.
   * All the different patterns are too confusing.
   * The ONLY functions being performed here are:
   * 1) Specify the image directory. ("png/")
   * 2) Load the "behind" ("..._B.png") filenames as needed.
   */
  public static String fixAccessoryFilename(String filename) {
    String path = String.format("png/%s.png", filename);

    File f = new File(path);
    if (f.exists()) {
      return String.format("%s.png", filename);
    } else {
      return String.format("%s_B.png", filename);
    }
  }
  
  public boolean drawBehind(int index) {
    return drawBehind[index];
  }
  
  
}
