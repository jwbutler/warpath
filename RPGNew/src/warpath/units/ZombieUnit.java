package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.Hashtable;

import jwbgl.*;
/* This is the BASE class for all zombie-type units. It'll be subclassed for anything that actually
 * appears in game (to specify combat stats, etc.) */
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.RPG;
import warpath.players.Player;

public abstract class ZombieUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking", "falling"};
  
  public ZombieUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "zombie", activities, new Hashtable<Color, Color>(), posn, player);
    this.setyOffset(-32);
  }  
  
  public ZombieUnit(RPG game, String name, String[] activities, Hashtable<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "zombie", activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }  
  
  public ZombieUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, defaultActivities, posn, player);
    this.setyOffset(-32);
  }
  
  public ZombieUnit(RPG game, String name, String animationName,
  String[] activities, Hashtable<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }

  @Override
  public void playHitSound() {
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
    /* This should be direction-dependent! */
    game.addObject(new Corpse(game, getPosn(), "zombie_falling_3.png"));
  }
  
  @Override
  /* Zombies have two directions for falling, denoted "falling" and "fallingB" rather
   * than with directions. Not sure which directions they specifically should correspond to.
   * (FU will) */
  public void loadFallingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.ZOMBIE_FALLING;
      String[] filenames2 = new String[filenames.length];
      if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = String.format("%s_%s_%s.png", animationName, "falling", animIndex);
        }
      } else {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = String.format("%s_%s_%s.png", animationName, "fallingB", animIndex);
        }
      }
      animations.add(new Animation(animationName, filenames2, "falling", dir, frames));
    }
  }
}
