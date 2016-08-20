package warpath.units;

import jwbgl.*;
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.players.Player;

import java.util.Arrays;
import java.util.List;

public class EnemySwordGuy extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private static final List<String> ACTIVITIES = Arrays.asList(
    "walking", "standing", "attacking", "stunned_short", "falling"
  );
  public EnemySwordGuy(String name, Posn posn, Player player) {
    super(name, ACTIVITIES, posn, player);
    currentHP = maxHP = 100;
    currentEP = maxEP = 40;
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
    RPG game = RPG.getInstance();
    super.nextActivity();
    BasicUnit targetUnit = getNextTargetUnit();
    if (currentActivity.equals("standing")) {
      for (BasicUnit u: game.getUnits()) {
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
  
  @Override
  public void doAttackHit(BasicUnit u) {
    RPG game = RPG.getInstance();
    int dmg = game.getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeHit(this, dmg);
    playHitSound();
  }
}
