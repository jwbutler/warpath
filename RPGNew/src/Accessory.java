import java.awt.Graphics;
import java.util.ArrayList;

import jwbgl.*;

/* An Accessory generally represents armor objects.  Could also be things like
 * beards. */
public abstract class Accessory {
  // we should make a generic class to subclass. there's a lot of copying and pasting
  // going on here - BUT i can't think of a way to do this without multiple
  // inheritance. fuck java
  private String slot;
  protected ArrayList<Animation> animations;
  protected String[] activities = {"walking", "standing", "attacking", "bashing", "blocking_1", "blocking_2", "blocking_3", "falling"};
  protected String animationName;
  protected RPG game;
  protected Unit unit;
  private Animation currentAnimation;
  protected int xOffset;
  protected int yOffset;
  
  public Accessory(RPG game, Unit unit, String animationName, String slot) {
    this.game = game;
    this.unit = unit;
    this.slot = slot;
    this.animationName = animationName;
    loadAnimations();
  }
  
  public void loadAnimations() {
    // c/p from Unit

    animations = new ArrayList<Animation>();
    for (int i = 0; i < activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
  }
  
  public void loadActivityAnimations(String activity) {
    if (activity.equals("falling")) {
      loadFallingAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  /* Why are we subtracting 20 here? */
  public void draw(Graphics g) {
    Posn pixel = game.gridToPixel(unit.getPosn()); // returns top left
    int left = pixel.getX() + RPG.TILE_WIDTH/2 - getSurface().getWidth()/2 + xOffset;
    int top = pixel.getY() + RPG.TILE_HEIGHT/2 - getSurface().getHeight()/2 + yOffset;
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
  
  /* C&P from Unit! */
  public void loadGenericAnimations(String activity) {
    String[] filenames = AnimationTemplates.getTemplate(activity);
    for (int i = 0; i < RPG.DIRECTIONS.length; i++) {
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String activityName = filenames[j].split("_")[0];
        String frameNum = filenames[j].split("_")[1];
        filenames2[j] = Animation.fixAccessoryFilename(String.format("%s_%s_%s_%s", animationName, activityName, RPG.DIRECTIONS[i], frameNum));
      }
      animations.add(new Animation(animationName, filenames2, activity, RPG.DIRECTIONS[i]));
    }
  }
  /* C&P from Unit.
   * IMPORTANT: Some of the equipment will have its own falling animations.
   * Need to override this. */
  public void loadFallingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
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
      animations.add(new Animation(animationName, filenames2, "falling", dir));
    }
  }

}
