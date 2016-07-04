package warpath.units;
import java.awt.Color;
import java.util.HashMap;

import jwbgl.*;
import warpath.core.RPG;
import warpath.players.Player;

/**
 * Female unit similar to SwordGuy but with no shield.
 */ 
public class SwordGirl extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private static String[] activities = {
    "walking", "standing", "attacking", "blocking1", "blocking2", "blocking3",
    "bashing", "slashing1", "slashing2", "slashing3"};

  public SwordGirl(String name, Posn posn, Player player) {
    super(name, "female", activities, new HashMap<Color, Color>(), posn, player);
    currentHP = maxHP = 100;
    minDamage = 5;
    maxDamage = 10;
    //addAccessory(new Sword(game, this, "sword_female"));
  }
  
  public void doAttackHit(Unit u) {
    RPG game = RPG.getInstance();
    int dmg = game.getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeDamage(dmg);
  }

}
