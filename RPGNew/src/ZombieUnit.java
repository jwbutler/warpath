import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import jwbgl.*;
/* This is the BASE class for all zombie-type units. It'll be subclassed for anything that actually
 * appears in game (to specify combat stats, etc.) */

public abstract class ZombieUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking", "falling"};
  
  public ZombieUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "zombie", activities, new HashMap<Color, Color>(), posn, player);
    this.setyOffset(-32);
  }  
  
  public ZombieUnit(RPG game, String name, String[] activities, HashMap<Color, Color> paletteSwaps,
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
  String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {
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
    /* This should be direction-dependent! */
    game.addObject(new Corpse(game, getPosn(), "zombie_falling_3.png"));
  }
  
  @Override
  /* Falling has a few variations; this is for human units I think.
   * Two directions, NE or S. */
  public void loadFallingAnimations() {
    String[] filenames = AnimationTemplates.ZOMBIE_FALLING;
    String[] filenames2 = new String[filenames.length];
    for (int i=0; i<filenames.length; i++) {
      filenames2[i] = Animation.fixFilename(animationName, filenames[i]);
    }
    for (int j=0; j<RPG.DIRECTIONS.length; j++) {
      String dir = RPG.DIRECTIONS[j];
      /* Using the special Animation constructor I made just for falling. */
      if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
        animations.add(new Animation(animationName, filenames2, "falling", dir));
      } else {
        animations.add(new Animation(animationName, filenames2, "fallingB", dir));
      }
    }
  }
}
