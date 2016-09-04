package warpath.units;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwbgl.*;
import warpath.activities.Activities;
import warpath.activities.Activity;
import warpath.internals.Direction;
import warpath.core.RPG;
import warpath.objects.Corpse;
import warpath.players.Player;

/**
 * This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots.
 */
public abstract class HumanUnit extends BasicUnit {
  private static final long serialVersionUID = 1L;
  private static final List<Activity> DEFAULT_ACTIVITIES = Arrays.asList(
    Activities.WALKING, Activities.STANDING, Activities.ATTACKING, Activities.FALLING
  );
  private static final String HIT_SOUND = "hit1.wav";
  private static final int Y_OFFSET = -32;
  
  public HumanUnit(String name, List<Activity> activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "player", activities, new HashMap<>(), posn, player);
  }  
  
  public HumanUnit(String name, List<Activity> activities, Map<Color, Color> playerUnitPaletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "player", activities, playerUnitPaletteSwaps, posn, player);
  }  
  
  public HumanUnit(String name, Posn posn, Player player) {
    this(name, DEFAULT_ACTIVITIES, posn, player);
  }
  
  public HumanUnit(String name, String spriteName,
  List<Activity> activities, Map<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(name, spriteName, activities, paletteSwaps, posn, player);
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
  public void setCurrentActivity(Activity newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  @Override
  public void die() {
    RPG game = RPG.getInstance();
    super.die();
    // TODO put this elsewhere
    List<Direction> neDirections = Arrays.asList(Direction.N,Direction.NE,Direction.E,Direction.SE);
    Direction dir = getCurrentDirection();
    if (neDirections.contains(dir)) {
      game.addObject(new Corpse(getPosn(), "player_falling_NE_4.png"));
    } else {
      game.addObject(new Corpse(getPosn(), "player_falling_S_4.png"));
    }
  }
}
