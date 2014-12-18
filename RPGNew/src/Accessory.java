import java.awt.Graphics;


public abstract class Accessory {
  // we should make a generic class to subclass. there's a lot of copying and pasting
  // going on here - BUT i can't think of a way to do this without multiple
  // inheritance. fuck java
  private String slot;
  private Animation[] animations;
  private String[] activities = {"walking", "standing", "attacking", "bashing"};
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
    // copy/pasted from Unit.
    animations = new Animation[activities.length * RPG.DIRECTIONS.length];
    int k = 0;
    for (int i = 0; i < activities.length; i++) {
      for (int j = 0; j < RPG.DIRECTIONS.length; j++) {
        String activity = activities[i];
        String[] filenames = AnimationTemplates.getTemplate(activity);
        animations[k++] = new Animation(animationName, filenames, activity, RPG.DIRECTIONS[j]);
      }
    }
  }
  
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
    while (i < animations.length) {
      if (!animations[i].getActivity().equals(activity)) {
        i += 8;
      } else if (!animations[i].getDirection().equals(direction)) {
        i++;
      } else {
        // It's a match!
        currentAnimation = animations[i];
        currentAnimation.setIndex(0);
        return;
      }
    }
    System.out.println("fuxxx");
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

}
