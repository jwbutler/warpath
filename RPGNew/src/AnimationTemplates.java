/* This class defines the basic set of animations that are used by basically
 * everything in the game.
 */

public class AnimationTemplates {
  public static final String[] STANDING = {
    "standing_1", "standing_1",
    "standing_1", "standing_1"};
  public static final String[] WALKING = {
    "walking_1", "walking_1",
    "walking_2","walking_2"};
  public static final String[] ATTACKING = {
    "attacking_1", "attacking_1",
    "attacking_2", "attacking_2",
    "attacking_2", "attacking_2",
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"};
  public static final String[] BLOCKING1 = {
    "attacking_1", "attacking_1",
    "attacking_2b", "attacking_2b"};
  public static final String[] BLOCKING2 = {
    "attacking_2b", "attacking_2b",
    "attacking_2b", "attacking_2b"};
  public static final String[] BLOCKING3 = {
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"};
  public static final String[] BASHING = {
    "attacking_1", "attacking_1",
    "attacking_2b", "attacking_2b",
    "attacking_2b", "attacking_2b",
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"};
  public static final String[] SLASHING1 = {
    "attacking_1", "attacking_1",
    "attacking_2", "attacking_2",
    };

  public static final String[] SLASHING3 = {
    "attacking_2", "attacking_2",
    "attacking_2", "attacking_2"};
     
  public static final String[] SLASHING2 = {
    "attacking_1", "attacking_1",
    "standing_1", "standing_1"};
     
  public static String[] getTemplate(String activity) {
    if (activity.equals("standing")) {
      return STANDING;
    } else if (activity.equals("walking")) {
      return WALKING;
    } else if (activity.equals("attacking")) {
      return ATTACKING;
    } else if (activity.equals("bashing")) {
      return BASHING;
    } else if (activity.equals("blocking1")) {
      return BLOCKING1;
    } else if (activity.equals("blocking2")) {
      return BLOCKING2;
    } else if (activity.equals("blocking3")) {
      return BLOCKING3;
    } else if (activity.equals("slashing1")) {
      return BLOCKING1;
    } else if (activity.equals("slashing2")) {
      return BLOCKING2;
    } else if (activity.equals("slashing3")) {
      return BLOCKING3;
    } else {
      return null;
    }
  }
}
