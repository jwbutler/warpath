package warpath.units;
import java.util.Random;

import jwbgl.*;
/* A basic enemy unit for testing! */
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.players.Player;

public class EnemyZombie extends ZombieUnit {
  private int minDamage, maxDamage;
  private final static double SLOW_MOVE_SPEED = 0.3;
  private final static double FAST_MOVE_SPEED = 0.7;
  private final static double ATTACK_CHANCE = 0.75;
  private final static int VISION_RADIUS = 6;
  private final static int SMELL_RADIUS = 12;
  private static String[] activities = {"walking", "standing", "attacking", "stunned_short", "falling"};
  public EnemyZombie(RPG game, String name, Posn posn, Player player) {
    super(game, name, activities, posn, player);
    currentHP = maxHP = 45;
    currentEP = maxEP = 30;
    minDamage = 2;
    maxDamage = 4;
    //minDamage = maxDamage = 0;
  }

  public void nextActivity() {
    super.nextActivity();
    Random RNG = game.getRNG();
    Unit tu = getNextTargetUnit();
    if (currentActivity.equals("standing")) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (Utils.distance2(this,u) <= SMELL_RADIUS) {
            /* This should be where the "smell" sound is played, but we're losing the target
             * due to endAttack() [maybe]. Work on this later */
            if (tu == null) {
              setNextTargetUnit(u);
              tu = u;
              //System.out.println("smell");
            } else if (Utils.distance2(this,u) < Utils.distance2(this,tu)) {
              setNextTargetUnit(u);
              tu = u;
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
      double cancelChance = 1-SLOW_MOVE_SPEED;
      tu = getNextTargetUnit();
      if (tu != null) {
        if (Utils.distance2(this, tu) <= VISION_RADIUS) {
          cancelChance = 1-FAST_MOVE_SPEED;
        } else {
          cancelChance = 1-SLOW_MOVE_SPEED;
        }
      }
      if (RNG.nextDouble() < cancelChance) {
        setCurrentActivity("standing");
        // HOW MUCH OF THIS IS NECESSARY?
        setTargetPosn(null);
        //setTargetUnit(null);
        //setNextTargetUnit(null);
        //setNextActivity(null);
      }
    } else if (currentActivity.equals("attacking")) {
      if (RNG.nextDouble() > ATTACK_CHANCE) {
        setCurrentActivity("standing");
        currentEP += ATTACK_COST;
        // HOW MUCH OF THIS IS NECESSARY?
        setTargetPosn(null);
      }
    }
  }
  
  public void doAttackHit(Unit u) {
    int dmg = game.getRNG().nextInt(maxDamage - minDamage + 1) + minDamage;
    // soundFX
    u.takeHit(this, dmg);
    playHitSound();
  }

  @Override
  public void playBashSound() {
    // TODO Auto-generated method stub
  }
}
