package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import jwbgl.*;
/* This is the BASE class for all zombie-type units. It'll be subclassed for anything that actually
 * appears in game (to specify combat stats, etc.) */
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.core.RPG;
import warpath.objects.Corpse;
import warpath.players.Player;

public abstract class ZombieUnit extends Unit implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String[] DEFAULT_ACTIVITIES = {"walking", "standing", "attacking", "falling"};
  private static final int X_OFFSET = 0;
  private static final int Y_OFFSET = -32;
  private static final String HIT_SOUND = "hit1.wav";
  
  
  public ZombieUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "zombie", activities, new HashMap<Color, Color>(), posn, player);
  }  
  
  public ZombieUnit(RPG game, String name, String[] activities, HashMap<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "zombie", activities, paletteSwaps, posn, player);
  }  
  
  public ZombieUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, DEFAULT_ACTIVITIES, posn, player);
  }
  
  public ZombieUnit(RPG game, String name, String animationName,
  String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    this.setXOffset(X_OFFSET);
    this.setYOffset(Y_OFFSET);
  }
  
  @Override
  public void die() {
    super.die();
    /* This should be direction-dependent! */
    game.addObject(new Corpse(game, getPosn(), "zombie_falling_3.png"));
  }
  
  @Override
  /* Zombies have two directions for falling, denoted "falling" and "fallingB" rather
   * than with directions. Not sure which directions they specifically should correspond to.
   * (FU will) */
  public void loadFallingAnimations() {
    for (int i=0; i<Constants.DIRECTIONS.length; i++) {
      String dir = Constants.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.ZOMBIE_FALLING;
      String[] filenames2 = new String[filenames.length];
      if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = String.format("%s_%s_%s.%s", animationName, "falling", animIndex, Constants.IMAGE_FORMAT);
        }
      } else {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = String.format("%s_%s_%s.%s", animationName, "fallingB", animIndex, Constants.IMAGE_FORMAT);
        }
      }
      animations.add(new Animation(animationName, filenames2, "falling", dir, frameCache));
    }
  }
  
  @Override
  public void playHitSound() {
    game.playSound(HIT_SOUND);
  }
}
