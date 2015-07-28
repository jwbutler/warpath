import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import jwbgl.*;

public abstract class RobedWizardUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {
    "standing", "walking", "falling", "teleporting", "appearing", "rezzing", "stunned_long"
    };
  
  public RobedWizardUnit(RPG game, String name, String animationName,
  String[] activities, Hashtable<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    yOffset = -32;
    hpBarOffset = -40;
    
  }
  
  public RobedWizardUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "robed_wizard", activities, new Hashtable<Color, Color>(), posn, player);
  }  
  
  public RobedWizardUnit(RPG game, String name, String[] activities, Hashtable<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "robed_wizard", activities, paletteSwaps, posn, player);
  }  
  
  public RobedWizardUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, defaultActivities, posn, player);
  }

  @Override
  public void setCurrentActivity(String newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  @Override
  public void die() {
    game.addObject(new Corpse(game, getPosn(), "robed_wizard_dead.png"));
  
  }
  
  @Override
  public void loadAnimations() {
    animations = new ArrayList<Animation>();
    for (int i=0; i<activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
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
    } else if (activity.equals("stunned_long")) {
      loadStunnedLongAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  public void loadStunnedLongAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_STUNNED_LONG;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "stunned", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "stunned_long", dir, frames));
    }
  }

  protected void loadAppearingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_APPEARING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "vanishing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "appearing", dir, frames));
    }
  }

  protected void loadTeleportingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_TELEPORTING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "vanishing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "teleporting", dir, frames));
    }

  }

  @Override
  /* (C&P from ZombieUnit)
   * Uses the vanishing animation which is directionless. */
  public void loadFallingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.FALLING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "vanishing", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "falling", dir, frames));
    }
  }
  
  /* Long (40-frame) casting animation, all SE. */
  public void loadRezzingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.WIZARD_REZZING;
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String animIndex = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "casting", "SE", animIndex);
      }
      animations.add(new Animation(animationName, filenames2, "rezzing", dir, frames));
    }
  }

}
