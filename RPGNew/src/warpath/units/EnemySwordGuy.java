package warpath.units;

import jwbgl.*;
/* A basic enemy unit for testing! */
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.players.Player;

public class EnemySwordGuy extends HumanUnit {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private static final String[] ACTIVITIES = {"walking", "standing", "attacking", "stunned_short", "falling"};
  public EnemySwordGuy(RPG game, String name, Posn posn, Player player) {
    super(game, name, ACTIVITIES, posn, player);
    currentHP = maxHP = 100;
    currentEP = maxEP = 40;
    minDamage = 5;
    maxDamage = 10;
  }

  /**
   * An extremely simple AI: simply finds the closest hostile unit and queues
   * up an attack, repeatedly.
   * Performs all the logic in {@link Unit#nextActivity}; then, if no action
   * has been started, issues the AI orders.
   * @see Unit#nextActivity
   */
  public void nextActivity() {
    super.nextActivity();
    Unit targetUnit = getNextTargetUnit();
    if (currentActivity.equals("standing")) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (targetUnit == null || Utils.distance2(this,u) < Utils.distance2(this,targetUnit)) {
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
