package warpath.animations;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jwbgl.*;
import warpath.core.Activity;
import warpath.internals.Direction;

/**
 * Represents a single animation of a unit.  Corresponds to a particular
 * activity and direction.  It's basically just a collection of Surface
 * objects.
 */

public class Animation {
  private final List<Surface> frames;
  
  // Used for items to determine whether they render in front of or behind the
  // main unit sprite.
  private final List<Boolean> drawBehind;
  private Activity activity;
  private Direction direction;
  private int index;
  
  public List<Surface> getFrames() {
    return frames;
  }

  public Activity getActivity() {
    return activity;
  }

  public Direction getDirection() {
    return direction;
  }

  /**
   * Creates a new Animation object.  Before loading a new image file, try
   * to retrieve from the frame cache object to avoid duplication.
   * @param spriteName - The name of the sprite used.
   * @param filenames - The list of filenames (not including extension or folder)
   * @param direction - The direction of the animation (e.g. "NW")
   * @param frameCache - The parent object's frame cache 
   */
  public Animation(String spriteName, Activity activity, Direction direction, List<String> filenames, Map<String, Surface> frameCache) {
    filenames.stream()
      .forEach(filename -> frameCache.computeIfAbsent(filename,
        f -> {
          Surface surface = new Surface(f).scale2x();
          surface.setColorkey(Color.WHITE);
          return surface;
        })
      );
    frames = filenames.stream()
      .map(filename -> frameCache.get(filename))
      .collect(Collectors.toList());
    drawBehind = filenames.stream()
      .map(filename -> filename.endsWith("_B.png"))
      .collect(Collectors.toList());
    this.activity = activity;
    this.direction = direction;
    index = 0;
  }

  public int getLength() {
    return frames.size();
  }
  
  public Surface getCurrentFrame() {
    return frames.get(index);
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
    return (index < frames.size() - 1);
  }
  
  public int getIndex() {
    return index;
  }
  
  /**
   * For debug purposes; outputs in the form (activity, direction, length)
   */
  public String toString() {
    return "<Animation("+getActivity()+","+getDirection()+","+getLength()+")>";
  }
  
  public boolean drawBehind(int index) {
    return drawBehind.get(index);
  }

  public static Animation fromTemplate(String spriteName, Activity activity, Direction direction, List<String> filenames,
  Map<String, Surface> frameCache, boolean isAccessory) {
    List<String> outFilenames = filenames.stream()
      .map(filename -> AnimationUtils.formatFilename(spriteName, filename.split("_")[0], direction, filename.split("_")[1], isAccessory))
      .collect(Collectors.toList());
    return new Animation(spriteName, activity, direction, outFilenames, frameCache);
  }

  public static Animation fromTemplate(String spriteName, Activity activity, Direction direction, List<String> filenames,
    Map<String, Surface> frameCache) {
    return Animation.fromTemplate(spriteName, activity, direction, filenames, frameCache, false);
  }

  // haaaack
  public void setDirection(Direction direction) {
    this.direction = direction;
  }
}