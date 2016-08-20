package warpath.units;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwbgl.*;
import warpath.core.RPG;
import warpath.objects.Corpse;
import warpath.players.Player;

/**
 * This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots.
 */
public abstract class HumanUnit extends BasicUnit {
  private static final long serialVersionUID = 1L;
  private static final List<String> DEFAULT_ACTIVITIES = Arrays.asList("walking", "standing", "attacking", "falling");
  private static final String HIT_SOUND = "hit1.wav";
  private static final int Y_OFFSET = -32;
  
  public HumanUnit(String name, List<String> activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "player", activities, new HashMap<>(), posn, player);
  }  
  
  public HumanUnit(String name, List<String> activities, Map<Color, Color> playerUnitPaletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "player", activities, playerUnitPaletteSwaps, posn, player);
  }  
  
  public HumanUnit(String name, Posn posn, Player player) {
    this(name, DEFAULT_ACTIVITIES, posn, player);
  }
  
  public HumanUnit(String name, String animationName,
  List<String> activities, Map<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(name, animationName, activities, paletteSwaps, posn, player);
    this.setYOffset(Y_OFFSET);
  }

  @Override
  public void playHitSound() {
    RPG.getInstance().playSound(HIT_SOUND);
  }
  
  @Override
  public void playBashSound() {
    RPG.getInstance().playSound(HIT_SOUND);
  }
  
  @Override
  public void setCurrentActivity(String newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  @Override
  public void die() {
    RPG game = RPG.getInstance();
    super.die();
    String dir = getCurrentDirection();
    if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
      game.addObject(new Corpse(getPosn(), "player_falling_NE_4.png"));
    } else {
      game.addObject(new Corpse(getPosn(), "player_falling_S_4.png"));
    }
  }
}
