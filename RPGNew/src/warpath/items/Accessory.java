package warpath.items;
import java.awt.Graphics;
import java.util.*;

import jwbgl.*;

import warpath.core.Activity;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.internals.Direction;
import warpath.core.RPG;
import warpath.units.Unit;

/**
 * A generic class for any unit accessories such as weapons, armor or hair.
 */
public abstract class Accessory {
  // we should make a generic class to subclass. there's a lot of copying and pasting
  // going on here - BUT i can't think of a way to do this without multiple
  // inheritance. fuck java
  private final ItemSlot slot;
  protected final Unit unit;
  protected final int xOffset;
  protected final int yOffset;
  protected final String spriteName;
  
  protected final Map<String, Surface> frameCache;

  protected final List<Animation> animations;
  protected final List<Activity> activities = Arrays.asList(
    Activity.WALKING, Activity.STANDING, Activity.ATTACKING, Activity.BASHING,
    Activity.BLOCKING_1, Activity.BLOCKING_2, Activity.BLOCKING_3, Activity.SLASHING_1,
    Activity.SLASHING_2, Activity.SLASHING_3, Activity.FALLING
  );
  
  private Animation currentAnimation;
  
  public Accessory(Unit unit, String spriteName, ItemSlot slot, int xOffset, int yOffset) {
    this.unit = unit;
    this.slot = slot;
    this.spriteName = spriteName;
    frameCache = new HashMap<>();

    animations = new ArrayList<>();
    loadAnimations();
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }
  public Accessory(Unit unit, String spriteName,ItemSlot slot) {
    this(unit, spriteName, slot, 0, 0);
  }
  
  /** Load all the animations for this object.
   * Calls loadActivityAnimations() for each activity.
   */
  public void loadAnimations() {
    for (Activity activity : activities) {
      loadActivityAnimations(activity);
    }
  }
    
  private void loadActivityAnimations(Activity activity) {
    if (activity.equals(Activity.FALLING)) {
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

  public void setCurrentAnimation(Activity activity, Direction direction) {
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

  public ItemSlot getSlot() {
    return slot;
  }
  
  public boolean drawBehind() {
    return getCurrentAnimation().drawBehind(getCurrentAnimation().getIndex());
  }
  
  /**
   * Load animations in the expected format used by Units.
   * C&P from BasicUnit!
   */
  public void loadGenericAnimations(Activity activity) {
    loadGenericAnimations(activity, AnimationTemplates.getTemplate(activity));
  }

  /**
   * Load animations in the expected format used by Units.
   * C&P from BasicUnit!
   */
  public void loadGenericAnimations(Activity activity, List<String> filenames) {
    for (Direction dir : Direction.directions()) {
      animations.add(Animation.fromTemplate(spriteName, activity, dir, filenames, frameCache, true));
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
    for (Direction dir : Direction.directions()) {
      List<Direction> neDirections = Arrays.asList(Direction.N, Direction.NE, Direction.E, Direction.SE);
      if (neDirections.contains(dir)) {
        animations.add(Animation.fromTemplate(spriteName, Activity.FALLING, Direction.NE, AnimationTemplates.FALLING, frameCache, true));
      } else {
        animations.add(Animation.fromTemplate(spriteName, Activity.FALLING, Direction.S, AnimationTemplates.FALLING, frameCache, true));
      }
    }
  }

}
