package warpath.units;

import jwbgl.*;

import warpath.activities.Activity;
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.players.Player;

import java.util.Arrays;
import java.util.List;

public class EnemySwordGuy extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private static final List<Activity> ACTIVITIES = Arrays.asList(
    Activity.WALKING, Activity.STANDING, Activity.ATTACKING, Activity.STUNNED_SHORT, Activity.FALLING
  );
  public EnemySwordGuy(String name, Posn posn, Player player) {
    super(name, ACTIVITIES, posn, player);
    setCurrentHP(100);
    setMaxHP(100);
    setCurrentEP(40);
    setMaxEP(40);
    minDamage = 5;
    maxDamage = 10;
  }

  /**
   * An extremely simple AI: simply finds the closest hostile unit and queues
   * up an attack, repeatedly.
   * Performs all the logic in {@link BasicUnit#nextActivity}; then, if no action
   * has been started, issues the AI orders.
   * @see BasicUnit#nextActivity
   */
  public void nextActivity() {
    super.nextActivity();
    Unit targetUnit = getNextTargetUnit();
    if (getCurrentActivity().equals(Activity.STANDING)) {
      for (Unit u: RPG.getInstance().getUnits()) {
        if (isHostile(u)) {
          if (targetUnit == null || Utils.distance(this, u) < Utils.distance(this, targetUnit)) {
            setNextTargetUnit(u);
          }
        }
      }
      if (getNextTargetUnit() != null) {
        setNextActivity(Activity.ATTACKING);
      }
      if (targetUnit != null) {
        super.nextActivity();
      }
    }
  }
  
  @Override
  public void doAttackHit(Unit u) {
    RPG game = RPG.getInstance();
    int dmg = game.getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeHit(this, dmg);
    playHitSound();
  }
}
