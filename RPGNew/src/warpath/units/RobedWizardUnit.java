package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.*;

import jwbgl.*;
import warpath.activities.Activities;
import warpath.activities.Activity;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.internals.Direction;
import warpath.core.RPG;
import warpath.items.Accessory;
import warpath.objects.Corpse;
import warpath.players.Player;

public abstract class RobedWizardUnit extends BasicUnit implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final List<Activity> ACTIVITIES = Arrays.asList(
    Activities.APPEARING, Activities.FALLING, Activities.REZZING, Activities.STANDING,
    Activities.STUNNED_SHORT, Activities.STUNNED_LONG, Activities.TELEPORTING, Activities.WALKING
  );
  private static final int X_OFFSET = 0;
  private static final int Y_OFFSET = -32;
  public static final int TELEPORT_COST = 200;
  public static final int REZ_COST = 200;
  
  public RobedWizardUnit(String name, String spriteName, List<Activity> activities, Map<Color, Color> paletteSwaps,
  Posn posn, Player player) {
    super(name, spriteName, activities, paletteSwaps, posn, player);
    setXOffset(X_OFFSET);
    setYOffset(Y_OFFSET);
    hpBarOffset = -40;
    
  }
  
  public RobedWizardUnit(String name, List<Activity> activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "robed_wizard", activities, new HashMap<>(), posn, player);
  }  
  
  public RobedWizardUnit(String name, List<Activity> activities, Map<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "robed_wizard", activities, paletteSwaps, posn, player);
  }  
  
  public RobedWizardUnit(String name, Posn posn, Player player) {
    this(name, ACTIVITIES, posn, player);
  }

  @Override
  public void setCurrentActivity(Activity newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  @Override
  public void die() {
    super.die();
    RPG game = RPG.getInstance();
    game.addObject(new Corpse(getPosn(), "robed_wizard_dead.png"));
  }
  
  @Override
  public void loadAnimations() {
    long t = System.currentTimeMillis();
    for (Activity activity : activities) {
      loadActivityAnimations(activity);
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
    t = System.currentTimeMillis() - t;
    System.out.printf("%s.loadAnimations(): %d ms\n", this.getClass(), t);
  }
  
  @Override
  // We can probably optimize this further but this is decently concise now.
  public void loadActivityAnimations(Activity activity) {
    if (activity.equals(Activities.FALLING)) {
      loadFallingAnimations();
    } else if (activity.equals(Activities.STANDING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STANDING);
    } else if (activity.equals(Activities.WALKING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_WALKING);
    } else if (activity.equals(Activities.TELEPORTING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_TELEPORTING);
    } else if (activity.equals(Activities.APPEARING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_APPEARING);
    } else if (activity.equals(Activities.REZZING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_REZZING);
    } else if (activity.equals(Activities.STUNNED_SHORT)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STUNNED_SHORT);
    } else if (activity.equals(Activities.APPEARING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STUNNED_LONG);
    } else {
      loadGenericAnimations(activity);
    }
  }

  public void loadFallingAnimations() {
    for (Direction dir : Direction.directions()) {
      animations.add(Animation.fromTemplate(spriteName, Activities.FALLING, dir, AnimationTemplates.WIZARD_FALLING, frameCache));
    }
  }
}
