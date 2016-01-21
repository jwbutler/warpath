package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import jwbgl.*;
import warpath.core.RPG;
import warpath.objects.Corpse;
import warpath.players.Player;

/**
 * This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots.
 */
public abstract class HumanUnit extends Unit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String[] DEFAULT_ACTIVITIES = {"walking", "standing", "attacking", "falling"};
  private static final String HIT_SOUND = "hit1.wav";
  private static final int Y_OFFSET = -32;
  
  public HumanUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, new HashMap<Color, Color>(), posn, player);
  }  
  
  public HumanUnit(RPG game, String name, String[] activities, HashMap<Color, Color> playerUnitPaletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, playerUnitPaletteSwaps, posn, player);
  }  
  
  public HumanUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, DEFAULT_ACTIVITIES, posn, player);
  }
  
  public HumanUnit(RPG game, String name, String animationName,
  String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    this.setYOffset(Y_OFFSET);
  }

  @Override
  public void playHitSound() {
    game.playSound(HIT_SOUND);
  }
  
  @Override
  public void playBashSound() {
    game.playSound(HIT_SOUND);
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
