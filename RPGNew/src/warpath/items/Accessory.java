package warpath.items;
import java.awt.Graphics;
import java.util.*;

import jwbgl.*;
import warpath.activities.ActivityNames;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.core.Direction;
import warpath.core.RPG;
import warpath.units.Unit;

/** A generic class for any unit accessories such as weapons, armor or hair. */
public abstract class Accessory {
  // we should make a generic class to subclass. there's a lot of copying and pasting
  // going on here - BUT i can't think of a way to do this without multiple
  // inheritance. fuck java
  private final String slot;
  protected final Unit unit;
  protected final int xOffset;
  protected final int yOffset;
  protected final String spriteName;
  
  protected final Map<String, Surface> frameCache;

  protected final List<Animation> animations;
  protected final List<String> activities = Arrays.asList(
    ActivityNames.WALKING, ActivityNames.STANDING, ActivityNames.ATTACKING, ActivityNames.BASHING,
    ActivityNames.BLOCKING_1, ActivityNames.BLOCKING_2, ActivityNames.BLOCKING_3, ActivityNames.SLASHING_1,
    ActivityNames.SLASHING_2, ActivityNames.SLASHING_3, ActivityNames.FALLING
  );
  
  private Animation currentAnimation;
  
  public Accessory(Unit unit, String spriteName, String slot, int xOffset, int yOffset) {
    this.unit = unit;
    this.slot = slot;
    this.spriteName = spriteName;
    frameCache = new HashMap<>();

    animations = new ArrayList<>();
    loadAnimations();
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }
  public Accessory(Unit unit, String spriteName, String slot) {
    this(unit, spriteName, slot, 0, 0);
  }
  
  /** Load all the animations for this object.
   * Calls loadActivityAnimations() for each activity.
   */
  public void loadAnimations() {
    for (String activity : activities) {
      loadActivityAnimations(activity);
    }
  }
    
  private void loadActivityAnimations(String activity) {
    if (activity.equals(ActivityNames.FALLING)) {
      loadFallingAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  /* Why are we subtracting 20 here? */
  public void draw(Graphics g) {
    Posn pixel = RPG.getInstance().gridToPixel(unit.getPosn()); // returns top left
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
  
  /**
   * Load animations in the expected format used by Units.
   * C&P from BasicUnit!
   */
  public void loadGenericAnimations(String activity) {
    loadGenericAnimations(activity, AnimationTemplates.getTemplate(activity));
  }

  /**
   * Load animations in the expected format used by Units.
   * C&P from BasicUnit!
   */
  public void loadGenericAnimations(String activity, List<String> filenames) {
    for (Direction dir : Direction.values()) {
      animations.add(Animation.fromTemplate(spriteName, activity, dir, filenames, frameCache));
    }
  }

  /**
   * Falling animations follow different rules than the usual BasicUnit falling
   * animations, so we will define them separately.
   * C&P from BasicUnit.
   * TODO: Some of the equipment will have its own falling animations.
   * Need to override this.
   */
  public void loadFallingAnimations() {
    for (Direction dir : Direction.values()) {
      List<Direction> neDirections = Arrays.asList(Direction.N, Direction.NE, Direction.E, Direction.SE);
      if (neDirections.contains(dir)) {
        animations.add(Animation.fromTemplate(spriteName, "falling", Direction.NE, AnimationTemplates.FALLING, frameCache));
      } else {
        animations.add(Animation.fromTemplate(spriteName, "falling", Direction.S, AnimationTemplates.FALLING, frameCache));
      }
    }
  }

}
