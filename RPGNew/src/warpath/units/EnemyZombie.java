package warpath.units;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jwbgl.*;
import warpath.activities.Activities;
import warpath.activities.Activity;
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.players.Player;

public class EnemyZombie extends ZombieUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private final static double SLOW_MOVE_SPEED = 0.3;
  private final static double FAST_MOVE_SPEED = 0.7;
  private final static double ATTACK_CHANCE = 0.75;
  private final static int VISION_RADIUS = 6;
  private final static int SMELL_RADIUS = 12;
  private static List<Activity> activities = Arrays.asList(
    Activities.WALKING, Activities.STANDING, Activities.ATTACKING, Activities.STUNNED_SHORT, Activities.FALLING
  );
  public EnemyZombie(String name, Posn posn, Player player) {
    super(name, activities, posn, player);
    currentHP = maxHP = 45;
    currentEP = maxEP = 30;
    minDamage = 2;
    maxDamage = 4;
    //minDamage = maxDamage = 0;
  }

  @Override
  public void nextActivity() {
    super.nextActivity();
    RPG game = RPG.getInstance();
    Random RNG = game.getRNG();
    Unit tu = getNextTargetUnit();
    if (currentActivity.equals(Activities.STANDING)) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (Utils.distance(this,u) <= SMELL_RADIUS) {
            /* This should be where the "smell" sound is played, but we're losing the target
             * due to endAttack() [maybe]. Work on this later */
            if (tu == null) {
              setNextTargetUnit(u);
              tu = u;
              //System.out.println("smell");
            } else if (Utils.distance(this,u) < Utils.distance(this,tu)) {
              setNextTargetUnit(u);
              tu = u;
            }
          }
        }
      }
      if (getNextTargetUnit() != null) {
        setNextActivity(Activities.ATTACKING);
      }
      if (targetUnit != null) {
        super.nextActivity();
      }
    }
    if (currentActivity.equals(Activities.WALKING)) {
      double cancelChance = 1 - SLOW_MOVE_SPEED;
      tu = getNextTargetUnit();
      if (tu != null) {
        if (Utils.distance(this, tu) <= VISION_RADIUS) {
          cancelChance = 1 - FAST_MOVE_SPEED;
        } else {
          cancelChance = 1 - SLOW_MOVE_SPEED;
        }
      }
      if (RNG.nextDouble() < cancelChance) {
        setCurrentActivity(Activities.STANDING);
        setTargetPosn(null);
      }
    } else if (currentActivity.equals(Activities.ATTACKING)) {
      if (RNG.nextDouble() > ATTACK_CHANCE) {
        setCurrentActivity(Activities.STANDING);
        currentEP += ATTACK_COST;
        // HOW MUCH OF THIS IS NECESSARY?
        setTargetPosn(null);
      }
    }
  }
  
  @Override
  public void doAttackHit(Unit u) {
    RPG game = RPG.getInstance();
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
