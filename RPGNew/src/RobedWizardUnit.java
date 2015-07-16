import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import jwbgl.*;

public abstract class RobedWizardUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking", "falling"};
  
  public RobedWizardUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, new HashMap<Color, Color>(), posn, player);
    this.setyOffset(-32);
  }  
  
  public RobedWizardUnit(RPG game, String name, String[] activities, HashMap<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }  
  
  public RobedWizardUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, defaultActivities, posn, player);
    this.setyOffset(-32);
  }
  
  public RobedWizardUnit(RPG game, String name, String animationName,
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
    String dir = getCurrentDirection();
    game.addObject(new Corpse(game, getPosn(), "robed_wizard_dead.png"));
  
  }
  
  @Override
  public void loadAnimations() {
    // we could easily rewrite this without k

    animations = new ArrayList<Animation>();
    for (int i = 0; i < activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
  }

}
