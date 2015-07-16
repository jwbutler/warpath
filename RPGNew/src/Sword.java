import java.util.ArrayList;

public class Sword extends Accessory {
  //ArrayList<String> behindFrameNames;

  public Sword(RPG game, Unit unit, String animationName) {
    super(game, unit, animationName, "mainhand");
    yOffset = -10;
    xOffset = 0; // just guessing.
  }
  
  public void loadAnimations() {
    // we could easily rewrite this without k

    animations = new ArrayList<Animation>();
    for (int i = 0; i < activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
  }
  
  /* Extend this as needed for animations that have different versions for
   * different units. Eventually that will likely be all of them. */
  public void loadActivityAnimations(String activity) {
    if (activity.equals("falling")) {
      loadFallingAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }

}
