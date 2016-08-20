package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwbgl.*;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.core.Direction;
import warpath.core.RPG;
import warpath.objects.Corpse;
import warpath.players.Player;

/**
 * This is the BASE class for all zombie-type units. It'll be subclassed for
 * anything that actually appears in game (to specify combat stats, etc.)
 */
public abstract class ZombieUnit extends BasicUnit implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final List<String> DEFAULT_ACTIVITIES = Arrays.asList("walking", "standing", "attacking", "falling");
  private static final int X_OFFSET = 0;
  private static final int Y_OFFSET = -32;
  private static final String HIT_SOUND = "hit1.wav";
  
  
  public ZombieUnit(String name, List<String> activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "zombie", activities, new HashMap<>(), posn, player);
  }  
  
  public ZombieUnit(String name, List<String> activities, Map<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "zombie", activities, paletteSwaps, posn, player);
  }  
  
  public ZombieUnit(String name, Posn posn, Player player) {
    this(name, DEFAULT_ACTIVITIES, posn, player);
  }
  
  public ZombieUnit(String name, String animationName,
  List<String> activities, Map<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(name, animationName, activities, paletteSwaps, posn, player);
    this.setXOffset(X_OFFSET);
    this.setYOffset(Y_OFFSET);
  }
  
  /**
   * TODO This should be direction-dependent!
   */
  @Override
  public void die() {
    super.die();
    RPG game = RPG.getInstance();
    game.addObject(new Corpse(getPosn(), "zombie_falling_3.png"));
  }
  
  /**
   * Zombies have two directions for falling, denoted "falling" and "fallingB"
   * rather than with directions. Not sure which directions they specifically
   * should correspond to. (FU Will)
   */
  @Override
  public void loadFallingAnimations() {
    for (Direction dir : Direction.directions()) {
      List<Direction> neDirections = Arrays.asList(Direction.N, Direction.NE, Direction.E, Direction.SE);
      if (neDirections.contains(dir)) {
        animations.add(Animation.fromTemplate(spriteName, "falling", null, AnimationTemplates.ZOMBIE_FALLING, frameCache));
      } else {
        animations.add(Animation.fromTemplate(spriteName, "fallingB", null, AnimationTemplates.ZOMBIE_FALLING, frameCache));
      }
    }
  }
  
  @Override
  public void playHitSound() {
    RPG.getInstance().playSound(HIT_SOUND);
  }
}
