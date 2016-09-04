package warpath.units;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jwbgl.*;
import warpath.activities.Activities;
import warpath.activities.Activity;
import warpath.core.RPG;
import warpath.players.Player;

/**
 * Female unit similar to SwordGuy but with no shield.
 */ 
public class SwordGirl extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  //
  private static final List<Activity> ACTIVITIES = Arrays.asList(
    Activities.WALKING, Activities.STANDING, Activities.ATTACKING, Activities.BASHING,
    Activities.BLOCKING_1, Activities.BLOCKING_2, Activities.BLOCKING_3, Activities.SLASHING_1,
    Activities.SLASHING_2, Activities.SLASHING_3, Activities.FALLING
  );

  public SwordGirl(String name, Posn posn, Player player) {
    super(name, "female", ACTIVITIES, new HashMap<>(), posn, player);
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
