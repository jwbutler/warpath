package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import jwbgl.*;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.items.Accessory;
import warpath.objects.Corpse;
import warpath.players.Player;

public abstract class RobedWizardUnit extends Unit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String[] DEFAULT_ACTIVITIES = {
    "standing", "walking", "falling", "teleporting", "appearing", "rezzing", "stunned_short", "stunned_long"
    };
  private static final int X_OFFSET = 0;
  private static final int Y_OFFSET = -32;
  
  public RobedWizardUnit(RPG game, String name, String animationName,
  String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    setXOffset(X_OFFSET);
    setYOffset(Y_OFFSET);
    hpBarOffset = -40;
    
  }
  
  public RobedWizardUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "robed_wizard", activities, new HashMap<Color, Color>(), posn, player);
  }  
  
  public RobedWizardUnit(RPG game, String name, String[] activities, HashMap<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "robed_wizard", activities, paletteSwaps, posn, player);
  }  
  
  public RobedWizardUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, DEFAULT_ACTIVITIES, posn, player);
  }

  @Override
  public void setCurrentActivity(String newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  @Override
  public void die() {
    super.die();
    game.addObject(new Corpse(game, getPosn(), "robed_wizard_dead.png"));
  
  }
  
  @Override
  public void loadAnimations() {
    long t = System.currentTimeMillis();
    for (int i=0; i<activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
    t = System.currentTimeMillis() - t;
    System.out.printf("%s.loadAnimations(): %d ms\n", this.getClass(), t);
  }
  
  public void loadActivityAnimations(String activity) {
    if (activity.equals("falling")) {
      loadFallingAnimations();
    } else if (activity.equals("standing")) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STANDING);
    } else if (activity.equals("walking")) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_WALKING);
    } else if (activity.equals("teleporting")) {
      loadTeleportingAnimations();
    } else if (activity.equals("appearing")) {
      loadAppearingAnimations();
    } else if (activity.equals("rezzing")) {
      loadRezzingAnimations();
    } else if (activity.equals("stunned_short")) {
      loadStunnedShortAnimations();
    } else if (activity.equals("stunned_long")) {
      loadStunnedLongAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  /* For now we're just using standing animations. Maybe we can do something better? */
  public void loadStunnedShortAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_STUNNED_SHORT;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "standing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "stunned_short", dir, frameCache));
    }
  }
  
  public void loadStunnedLongAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_STUNNED_LONG;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "stunned", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "stunned_long", dir, frameCache));
    }
  }

  protected void loadAppearingAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_APPEARING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "vanishing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "appearing", dir, frameCache));
    }
  }

  protected void loadTeleportingAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_TELEPORTING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "vanishing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "teleporting", dir, frameCache));
    }

  }

  @Override
  /* (C&P from ZombieUnit)
   * Uses the vanishing animation which is directionless. */
  public void loadFallingAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.FALLING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "vanishing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "falling", dir, frameCache));
    }
  }
  
  /* Long (40-frame) casting animation, all SE. */
  public void loadRezzingAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_REZZING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "casting", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "rezzing", dir, frameCache));
    }
  }

}
