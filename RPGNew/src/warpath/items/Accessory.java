package warpath.items;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;

import jwbgl.*;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.units.Unit;

/** A generic class for any unit accessories such as weapons, armor or hair. */
public abstract class Accessory {
  // we should make a generic class to subclass. there's a lot of copying and pasting
  // going on here - BUT i can't think of a way to do this without multiple
  // inheritance. fuck java
  private final String slot;
  protected final RPG game;
  protected final Unit unit;
  protected final int xOffset;
  protected final int yOffset;
  protected final String animationName;
  
  protected final Hashtable<String, Surface> frames;
  protected final ArrayList<Animation> animations;
  protected final String[] activities = {
    "walking", "standing", "attacking", "bashing", "blocking_1", "blocking_2", "blocking_3",
    "slashing_1", "slashing_2", "slashing_3", "falling"};
  private Animation currentAnimation;
  
  public Accessory(RPG game, Unit unit, String animationName, String slot, int xOffset, int yOffset) {
    this.game = game;
    this.unit = unit;
    this.slot = slot;
    this.animationName = animationName;
    frames = new Hashtable<String, Surface>();

    animations = new ArrayList<Animation>();
    loadAnimations();
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }
  public Accessory(RPG game, Unit unit, String animationName, String slot) {
    this(game, unit, animationName, slot, 0, 0);
  }
  
  /** Load all the animations for this object.
   * Calls loadActivityAnimations() for each activity.
   */
  public void loadAnimations() {
    for (int i = 0; i < activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
  }
    
  private void loadActivityAnimations(String activity) {
    if (activity.equals("falling")) {
      loadFallingAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  /* Why are we subtracting 20 here? */
  public void draw(Graphics g) {
    Posn pixel = game.gridToPixel(unit.getPosn()); // returns top left
    int left = pixel.getX() + Constants.TILE_WIDTH/2 - getSurface().getWidth()/2 + xOffset;
    int top = pixel.getY() + Constants.TILE_HEIGHT/2 - getSurface().getHeight()/2 + yOffset;
    top -= 20;
    getSurface().draw(g, left, top);
  }
  
  public Surface getSurface() {
    return getCurrentAnimation().getCurrentFrame();
  }
  
  public Animation getCurrentAnimation() {
    return currentAnimation;
  }

  public void setCurrentAnimation(String activity, String direction) {
    int i = 0;
    while (i < animations.size()) {
      if (!animations.get(i).getActivity().equals(activity)) {
        i += 8;
      } else if (!animations.get(i).getDirection().equals(direction)) {
        i++;
      } else {
        // It's a match!
        currentAnimation = animations.get(i);
        currentAnimation.setIndex(0);
        return;
      }
    }
  }

  public void nextFrame() {
    currentAnimation.nextFrame();
  }

  public String getSlot() {
    return slot;
  }
  
  public boolean drawBehind() {
    return getCurrentAnimation().drawBehind(getCurrentAnimation().getIndex());
  }
  
  /** Load animations in the expected format used by Units.
   * C&P from Unit! */
  protected void loadGenericAnimations(String activity) {
    String[] filenames = AnimationTemplates.getTemplate(activity);
    for (int i = 0; i < Constants.DIRECTIONS.length; i++) {
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String activityName = filenames[j].split("_")[0];
        String frameNum = filenames[j].split("_")[1];
        filenames2[j] = Animation.fixAccessoryFilename(String.format("%s_%s_%s_%s", animationName, activityName, Constants.DIRECTIONS[i], frameNum));
      }
      animations.add(new Animation(animationName, filenames2, activity, Constants.DIRECTIONS[i], frames));
    }
  }
  /** Falling animations follow different rules than the usual Unit falling
   * animations, so we will define them separately.
   * C&P from Unit.
   * TODO: Some of the equipment will have its own falling animations.
   * Need to override this. */
  protected void loadFallingAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.FALLING;
      String[] filenames2 = new String[filenames.length];
      if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = Animation.fixAccessoryFilename(String.format("%s_%s_%s_%s", animationName, "falling", "NE", animIndex));

        }
      } else {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = Animation.fixAccessoryFilename(String.format("%s_%s_%s_%s", animationName, "falling", "S", animIndex));
        }
      }
      animations.add(new Animation(animationName, filenames2, "falling", dir, frames));
    }
  }

}
