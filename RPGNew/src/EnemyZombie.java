import java.util.Random;

import jwbgl.*;
/* A basic enemy unit for testing! */

public class EnemyZombie extends ZombieUnit {
  private int minDamage, maxDamage;
  private double slowMoveSpeed, fastMoveSpeed, attackChance;
  private int visionRadius, smellRadius;
  private Random RNG;
  private static String[] activities = {"walking", "standing", "attacking", "stunned_short", "falling"};
  public EnemyZombie(RPG game, String name, Posn posn, Player player) {
    super(game, name, activities, posn, player);
    currentHP = maxHP = 100;
    currentEP = maxEP = 40;
    minDamage = 5;
    maxDamage = 10;
    slowMoveSpeed = 0.3;
    fastMoveSpeed = 0.6;
    attackChance = 0.7;
    visionRadius = 10;
    smellRadius = 20;
    RNG = new Random();
  }

  public void nextActivity() {
    super.nextActivity();
    Unit targetUnit = getNextTargetUnit();
    if (currentActivity.equals("standing")) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (game.distance2(this,u) <= smellRadius) {
            if (targetUnit == null) {
              setNextTargetUnit(u);
            } else if (game.distance2(this,u) < game.distance2(this,targetUnit)) {
              setNextTargetUnit(u);
            }
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
    if (currentActivity.equals("walking")) {
      if (RNG.nextDouble() > fastMoveSpeed) {
        setCurrentActivity("standing");
        // HOW MUCH OF THIS IS NECESSARY?
        setTargetPosn(null);
        setNextTargetPosn(null);
        //setTargetUnit(null);
        //setNextTargetUnit(null);
        //setNextActivity(null);
      }
    } else if (currentActivity.equals("attacking")) {
      if (RNG.nextDouble() > attackChance) {
        setCurrentActivity("standing");
        // HOW MUCH OF THIS IS NECESSARY?
        setTargetPosn(null);
        setNextTargetPosn(null);
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
