package warpath.units;

import jwbgl.*;
import warpath.core.RPG;
import warpath.players.Player;

public class EnemyTargetDummy extends HumanUnit {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private static String[] activities = {"walking", "standing", "attacking", "stunned_short"};
  public EnemyTargetDummy(RPG game, String name, Posn posn, Player player) {
    super(game, name, activities, posn, player);
    currentHP = maxHP = 100;
    minDamage = 5;
    maxDamage = 10;
  }

  public void nextActivity() {
    super.nextActivity();
    /*if (currentActivity.equals("standing")) {
      for (Unit u: game.getUnits()) {
        if (isHostile(u)) {
          if (targetUnit == null || game.distance(this,u) < game.distance(this,targetUnit)) {
            nextTargetUnit = u;
            nextActivity = "attacking";
          }
        }
      }
      if (targetUnit != null) {
        super.nextActivity();
      }
    }*/
  }
}
