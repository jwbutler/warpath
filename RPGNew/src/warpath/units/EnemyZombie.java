package warpath.units;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jwbgl.*;

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
  private final static List<Activity> ACTIVITIES = Arrays.asList(
    Activity.WALKING, Activity.STANDING, Activity.ATTACKING, Activity.STUNNED_SHORT, Activity.FALLING
  );
  public EnemyZombie(String name, Posn posn, Player player) {
    super(name, ACTIVITIES, posn, player);
    setCurrentHP(45);
    setMaxHP(45);
    setCurrentEP(30);
    setMaxEP(30);
    minDamage = 2;
    maxDamage = 4;
    //minDamage = maxDamage = 0;
  }

  @Override
  public void nextActivity() {
    super.nextActivity();
    RPG game = RPG.getInstance();
    Random RNG = game.getRNG();
    Unit targetUnit = getNextTargetUnit();
    if (getCurrentActivity().equals(Activity.STANDING)) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (Utils.distance(this,u) <= SMELL_RADIUS) {
            // TODO This should be where the "smell" sound is played, but we're
            // losing the target due to endAttack() [maybe]. Work on this later
            if (targetUnit == null) {
              setNextTargetUnit(u);
              targetUnit = u;
              //System.out.println("smell");
            } else if (Utils.distance(this,u) < Utils.distance(this,targetUnit)) {
              setNextTargetUnit(u);
              targetUnit = u;
            }
          }
        }
      }
      if (getNextTargetUnit() != null) {
        setNextActivity(Activity.ATTACKING);
      }
      if (getTargetUnit() != null) {
        super.nextActivity();
      }
    }
    if (getCurrentActivity().equals(Activity.WALKING)) {
      double cancelChance = 1 - SLOW_MOVE_SPEED;
      targetUnit = getNextTargetUnit();
      if (targetUnit != null) {
        if (Utils.distance(this, targetUnit) <= VISION_RADIUS) {
          cancelChance = 1 - FAST_MOVE_SPEED;
        } else {
          cancelChance = 1 - SLOW_MOVE_SPEED;
        }
      }
      if (RNG.nextDouble() < cancelChance) {
        setCurrentActivity(Activity.STANDING);
        setTargetPosn(null);
      }
    } else if (getCurrentActivity().equals(Activity.ATTACKING)) {
      if (RNG.nextDouble() > ATTACK_CHANCE) {
        setCurrentActivity(Activity.STANDING);
        currentEP += ATTACK_COST;
        // HOW MUCH OF THIS IS NECESSARY?
        setTargetPosn(null);
      }
    }
   //System.out.println(this);
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
