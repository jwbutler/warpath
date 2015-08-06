/* This class defines the basic set of animations that are used by basically
 * everything in the game.
 */
public class AnimationTemplates {
  public static final String[] STANDING = {
    "standing_1", "standing_1",
    "standing_1", "standing_1"
  };
  
  /* 8 frames */
  public static final String[] STUNNED_SHORT = {
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1"
  };
  
  /* 16 frames */
  public static final String[] STUNNED_LONG = {
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1"
  };
  
  /* 40 frames */
  public static final String[] WIZARD_STUNNED_LONG = {
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
  };
  
  public static final String[] WALKING = {
    "walking_1", "walking_1",
    "walking_2", "walking_2",
    "standing_1", "standing_1"
  };
  
  public static final String[] ATTACKING = {
    "attacking_1", "attacking_1",
    "attacking_2", "attacking_2",
    "attacking_2", "attacking_2",
    "attacking_2", "attacking_2",
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"
  };
  
  public static final String[] BLOCKING_1 = {
    "attacking_1", "attacking_1"
  };
  
  public static final String[] BLOCKING_2 = {
    "attacking_2b", "attacking_2b"
  };
  
  public static final String[] BLOCKING_3 = {
    "attacking_1", "attacking_1"
  };
  
  public static final String[] BASHING = {
    "attacking_1", "attacking_1",
    "attacking_2b", "attacking_2b",
    "attacking_2b", "attacking_2b",
    "attacking_2b", "attacking_2b",
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"
  };
  
  public static final String[] SLASHING_1 = {
    "attacking_1", "attacking_1",
  };
  
  public static final String[] SLASHING_2 = {
    "attacking_2", "attacking_2",
  };
  
  public static final String[] SLASHING_3 = {
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"
  };
  
  /* HACK - this is really only 8 frames but the game tries to draw the unit
   * before removing it. So we're going to give it an extra pair of frames. */
  public static final String[] FALLING = {
    "falling_1", "falling_1",
    "falling_2", "falling_2",
    "falling_3", "falling_3",
    "falling_4", "falling_4",
    "falling_4", "falling_4"
  };
  
  public static final String[] ZOMBIE_FALLING = {
    "falling_1", "falling_1",
    "falling_2", "falling_2",
    "falling_3", "falling_3",
    "falling_3", "falling_3"
  };
  
  public static final String[] WIZARD_STANDING = {
    "standing_1", "standing_1",
    "standing_2", "standing_2",
    "standing_3", "standing_3",
    "standing_4", "standing_4"
  };
  
  public static final String[] WIZARD_WALKING = {
    "walking_1", "walking_1",
    "walking_1", "walking_1"
  };

  public static final String[] WIZARD_APPEARING = {
    "vanishing_4", "vanishing_4",
    "vanishing_3", "vanishing_3",
    "vanishing_2", "vanishing_2",
    "vanishing_1", "vanishing_1"
  };

  public static String[] WIZARD_TELEPORTING = {
    "vanishing_1", "vanishing_1",
    "vanishing_2", "vanishing_2",
    "vanishing_3", "vanishing_3",
    "vanishing_4", "vanishing_4"
  };
  
  /* 40 frames */
  public static String[] WIZARD_REZZING = {
    "casting_1", "casting_1", "casting_1", "casting_1",
    "casting_2", "casting_2", "casting_2", "casting_2",
    "casting_3", "casting_3", "casting_3", "casting_3",
    "casting_4", "casting_4", "casting_4", "casting_4",
    "casting_5", "casting_5", "casting_5", "casting_5",
    "casting_6", "casting_6", "casting_6", "casting_6",
    "casting_7", "casting_7", "casting_7", "casting_7",
    "casting_8", "casting_8", "casting_8", "casting_8",
    "casting_5", "casting_5", "casting_5", "casting_5",
    "casting_6", "casting_6", "casting_6", "casting_6",
    "casting_7", "casting_7", "casting_7", "casting_7",
    "casting_8", "casting_8", "casting_8", "casting_8"
  };
  
  /* This is a convenience method to load the default Player animations
   * semi-automatically. */
  public static String[] getTemplate(String activity) {
    if (activity.equals("standing")) {
      return STANDING;
    } else if (activity.equals("walking")) {
      return WALKING;
    } else if (activity.equals("attacking")) {
      return ATTACKING;
    } else if (activity.equals("stunned_short")) {
      return STUNNED_SHORT;
    } else if (activity.equals("stunned_long")) {
      return STUNNED_LONG;
    } else if (activity.equals("bashing")) {
      return BASHING;
    } else if (activity.equals("blocking_1")) {
      return BLOCKING_1;
    } else if (activity.equals("blocking_2")) {
      return BLOCKING_2;
    } else if (activity.equals("blocking_3")) {
      return BLOCKING_3;
    } else if (activity.equals("slashing_1")) {
      return SLASHING_1;
    } else if (activity.equals("slashing_2")) {
      return SLASHING_2;
    } else if (activity.equals("slashing_3")) {
      return SLASHING_3;
    } else if (activity.equals("falling")) {
      return FALLING;
    } else {
      return null;
    }
  }
}
