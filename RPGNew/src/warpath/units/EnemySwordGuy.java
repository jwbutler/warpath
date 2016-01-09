package warpath.units;
import java.util.Random;

import jwbgl.*;
/* A basic enemy unit for testing! */
import warpath.core.RPG;
import warpath.players.Player;

public class EnemySwordGuy extends HumanUnit {
  private int minDamage, maxDamage;
  private static String[] activities = {"walking", "standing", "attacking", "stunned_short", "falling"};
  public EnemySwordGuy(RPG game, String name, Posn posn, Player player) {
    super(game, name, activities, posn, player);
    currentHP = maxHP = 100;
    currentEP = maxEP = 40;
    minDamage = 5;
    maxDamage = 10;
  }

  public void nextActivity() {
    super.nextActivity();
    Unit targetUnit = getNextTargetUnit();
    if (currentActivity.equals("standing")) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (targetUnit == null || game.distance2(this,u) < game.distance2(this,targetUnit)) {
            setNextTargetUnit(u);
          }
        }
      }
      if (getNextTargetUnit() != null) {
        setNextActivity("attacking");
      }
      if (targetUnit != null) {
        super.nextActivity();
      }
    }
  }
  
  public void doAttackHit(Unit u) {
    int dmg = game.getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeHit(this, dmg);
    playHitSound();
  }
}
