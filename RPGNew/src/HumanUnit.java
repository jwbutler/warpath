import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import jwbgl.*;
/* This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots. */

public class HumanUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking"};
  
  public HumanUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, new HashMap<Color, Color>(), posn, player);
    this.setyOffset(-32);
  }  
  
  public HumanUnit(RPG game, String name, String[] activities, HashMap<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }  
  
  public HumanUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, defaultActivities, posn, player);
    this.setyOffset(-32);
  }
  
  public HumanUnit(RPG game, String name, String animationName,
  String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }
}
