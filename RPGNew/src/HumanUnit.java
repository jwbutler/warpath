import java.io.Serializable;

/* This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots. */

public class HumanUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking"};
  
  public HumanUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, posn, player);
    this.setyOffset(-32);
  }  
  
  public HumanUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, defaultActivities, posn, player);
    this.setyOffset(-32);
  }
  
  public HumanUnit(RPG game, String name, String animationName,
  String[] activities, Posn posn, Player player) {
    super(game, name, animationName, activities, posn, player);
    this.setyOffset(-32);
  }
}
