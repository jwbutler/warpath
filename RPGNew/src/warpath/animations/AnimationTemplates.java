package warpath.animations;

import warpath.core.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Defines the filenames for the basic set of animations that are used by
 * basically all the units in the game.
 */
public class AnimationTemplates {
  public static final List<String> ATTACKING = Arrays.asList(
    "attacking_1", "attacking_1",
    "attacking_2", "attacking_2", "attacking_2", "attacking_2", "attacking_2", "attacking_2",
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"
  );

  // 12 frames
  public static final List<String> BASHING = Arrays.asList(
    "attacking_1", "attacking_1",
    "attacking_2b", "attacking_2b", "attacking_2b", "attacking_2b", "attacking_2b", "attacking_2b",
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"
  );

  //2 frames
  public static final List<String> BLOCKING_1 = Arrays.asList(
    "attacking_1", "attacking_1"
  );

  //2 frames
  public static final List<String> BLOCKING_2 = Arrays.asList(
    "attacking_2b", "attacking_2b"
  );
  //2 frames
  public static final List<String> BLOCKING_3 = Arrays.asList(
    "attacking_1", "attacking_1"
  );

  // HACK - this is really only 8 frames but the game tries to draw the unit
  // before removing it. So we're going to give it an extra pair of frames.
  public static final List<String> FALLING = Arrays.asList(
    "falling_1", "falling_1",
    "falling_2", "falling_2",
    "falling_3", "falling_3",
    "falling_4", "falling_4",
    "falling_4", "falling_4"
  );

  // 2 frames
  public static final List<String> SLASHING_1 = Arrays.asList(
    "attacking_1", "attacking_1"
  );

  // 2 frames
  public static final List<String> SLASHING_2 = Arrays.asList(
    "attacking_2", "attacking_2"
  );

  // 4 frames
  public static final List<String> SLASHING_3 = Arrays.asList(
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"
  );

  // 4 frames
  public static final List<String> STANDING = Arrays.asList(
    "standing_1", "standing_1",
    "standing_1", "standing_1"
  );

  // 8 frames
  public static final List<String> STUNNED_SHORT = Arrays.asList(
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1"
  );

  // 16 frames
  public static final List<String> STUNNED_LONG = Arrays.asList(
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1",
    "standing_1", "standing_1"
  );

  // 6 frames
  public static final List<String> WALKING = Arrays.asList(
    "walking_1", "walking_1",
    "walking_2", "walking_2",
    "standing_1", "standing_1"
  );

  public static final List<String> WIZARD_APPEARING = Arrays.asList(
    "vanishing_4", "vanishing_4",
    "vanishing_3", "vanishing_3",
    "vanishing_2", "vanishing_2",
    "vanishing_1", "vanishing_1"
  );

  public static final List<String> WIZARD_STANDING = Arrays.asList(
    "standing_1", "standing_1",
    "standing_2", "standing_2",
    "standing_3", "standing_3",
    "standing_4", "standing_4"
  );

  // 8 frames
  public static final List<String> WIZARD_STUNNED_SHORT = Arrays.asList(
    "standing_1", "standing_1",
    "standing_2", "standing_2",
    "standing_3", "standing_3",
    "standing_4", "standing_4"
  );

  // 40 frames
  public static final List<String> WIZARD_STUNNED_LONG = Arrays.asList(
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2",
    "stunned_1", "stunned_1", "stunned_1", "stunned_1",
    "stunned_2", "stunned_2", "stunned_2", "stunned_2"
  );

  public static List<String> WIZARD_TELEPORTING = Arrays.asList(
    "vanishing_1", "vanishing_1",
    "vanishing_2", "vanishing_2",
    "vanishing_3", "vanishing_3",
    "vanishing_4", "vanishing_4"
  );

  // 40 frames
  public static List<String> WIZARD_REZZING = Arrays.asList(
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
  );

  public static final List<String> WIZARD_WALKING = Arrays.asList(
    "walking_1", "walking_1",
    "walking_1", "walking_1",
    "walking_1", "walking_1",
    "walking_1", "walking_1",
    "walking_1", "walking_1"
  );

  public static final List<String> WIZARD_FALLING = Arrays.asList(
    "vanishing_1", "vanishing_1",
    "vanishing_2", "vanishing_2",
    "vanishing_3", "vanishing_3",
    "vanishing_4", "vanishing_4"
  );

  public static final List<String> ZOMBIE_FALLING = Arrays.asList(
    "falling_1", "falling_1",
    "falling_2", "falling_2",
    "falling_3", "falling_3",
    "falling_3", "falling_3"
  );

  private static final List<String> ZOMBIE_FALLING_ALT = Arrays.asList(
    "fallingB_1", "fallingB_1",
    "fallingB_2", "fallingB_2",
    "fallingB_3", "fallingB_3",
    "fallingB_3", "fallingB_3"
  );

  /**
   * This is a convenience method to load the default Player animations
   * semi-automatically.  Super redundant, will make more sense when we
   * switch to the Enum.
   */
  public static List<String> getTemplate(Activity activity) {
    if (activity.equals(Activity.STANDING)) {
      return STANDING;
    } else if (activity.equals(Activity.WALKING)) {
      return WALKING;
    } else if (activity.equals(Activity.ATTACKING)) {
      return ATTACKING;
    } else if (activity.equals(Activity.STUNNED_SHORT)) {
      return STUNNED_SHORT;
    } else if (activity.equals(Activity.STUNNED_LONG)) {
      return STUNNED_LONG;
    } else if (activity.equals(Activity.BASHING)) {
      return BASHING;
    } else if (activity.equals(Activity.BLOCKING_1)) {
      return BLOCKING_1;
    } else if (activity.equals(Activity.BLOCKING_2)) {
      return BLOCKING_2;
    } else if (activity.equals(Activity.BLOCKING_3)) {
      return BLOCKING_3;
    } else if (activity.equals(Activity.SLASHING_1)) {
      return SLASHING_1;
    } else if (activity.equals(Activity.SLASHING_2)) {
      return SLASHING_2;
    } else if (activity.equals(Activity.SLASHING_3)) {
      return SLASHING_3;
    } else if (activity.equals(Activity.FALLING)) {
      return FALLING;
    } else if (activity.equals(Activity.ZOMBIE_FALLING_ALT)) {
      return ZOMBIE_FALLING_ALT;
    } else {
      System.out.println("fuck fuck fuck " + activity);
      return new ArrayList<>();
    }
  }
}
