package warpath.units;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jwbgl.*;
import warpath.activities.ActivityNames;
import warpath.core.RPG;
import warpath.players.Player;

/**
 * Female unit similar to SwordGuy but with no shield.
 */ 
public class SwordGirl extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  //
  private static final List<String> ACTIVITIES = Arrays.asList(
    ActivityNames.WALKING, ActivityNames.STANDING, ActivityNames.ATTACKING, ActivityNames.BASHING,
    ActivityNames.BLOCKING_1, ActivityNames.BLOCKING_2, ActivityNames.BLOCKING_3, ActivityNames.SLASHING_1,
    ActivityNames.SLASHING_2, ActivityNames.SLASHING_3, ActivityNames.FALLING
  );

  public SwordGirl(String name, Posn posn, Player player) {
    super(name, "female", ACTIVITIES, new HashMap<Color, Color>(), posn, player);
    currentHP = maxHP = 100;
    minDamage = 5;
    maxDamage = 10;
    //addAccessory(new Sword(game, this, "sword_female"));
  }
  
  public void doAttackHit(BasicUnit u) {
    RPG game = RPG.getInstance();
    int dmg = game.getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeDamage(dmg);
  }

}
