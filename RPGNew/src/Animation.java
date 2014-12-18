import java.awt.Color;
import java.io.File;

/* Represents a single animation of a unit.  Corresponds to a particular
 * activity and direction.  It's basically just a collection of Surface
 * objects. */

public class Animation {
  private Surface[] frames;
  private Boolean[] drawBehind;
  private String activity;
  private String direction;
  private int index;
  
  public String getActivity() {
    return activity;
  }

  public String getDirection() {
    return direction;
  }

  public Animation(String animName, String[] filenames, String activity, String direction) {
    frames = new Surface[filenames.length];
    drawBehind = new Boolean[filenames.length];
    for (int i = 0; i < filenames.length; i++) {
      String filename = fixFilename(animName, filenames[i], direction);
      if (filename.endsWith("_B.png")) {
        drawBehind[i] = true;
      } else {
        drawBehind[i] = false;
      }
      frames[i] = new Surface(filename);
      frames[i] = frames[i].scale2x();
      frames[i].setColorkey(Color.WHITE);
    }
    this.activity = activity;
    this.direction = direction;
    index = 0;
  }
  
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
  
  // Given a string like "walking_1" and a direction "N", construct a complete filename
  // ("walking_N_1.png").
  public String fixFilename(String animName, String filename, String direction) {
    String fixedFilename = animName + "_" + filename.split("_")[0] + "_" +
    direction + "_" + filename.split("_")[1] + ".png";
    String path = "bin" + File.separator + "png" + File.separator + fixedFilename;

    File f = new File(path);
    if (f.exists()) {
      return fixedFilename;
    } else {
      return fixedFilename.split(".png")[0] + "_B.png";
    }
  }
  
  public boolean drawBehind(int index) {
    return drawBehind[index];
  }
  
  
}
