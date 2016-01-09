package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.Hashtable;

import jwbgl.*;
/* This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots. */
import warpath.core.RPG;
import warpath.players.Player;

public abstract class HumanUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking", "falling"};
  
  public HumanUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, new Hashtable<Color, Color>(), posn, player);
    this.setyOffset(-32);
  }  
  
  public HumanUnit(RPG game, String name, String[] activities, Hashtable<Color, Color> paletteSwaps,
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
  String[] activities, Hashtable<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }

  @Override
  public void playHitSound() {
    game.playSound("hit1.wav");
  }
  
  @Override
  public void playBashSound() {
    game.playSound("hit1.wav");
  }
  
  @Override
  public void setCurrentActivity(String newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  @Override
  public void die() {
    super.die();
    String dir = getCurrentDirection();
    if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
      game.addObject(new Corpse(game, getPosn(), "player_falling_NE_4.png"));
    } else {
      game.addObject(new Corpse(game, getPosn(), "player_falling_S_4.png"));
    }
  }
}
