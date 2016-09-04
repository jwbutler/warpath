package warpath.units;

import jwbgl.*;
import warpath.activities.Activities;
import warpath.activities.Activity;
import warpath.players.Player;

import java.util.Arrays;
import java.util.List;

public class EnemyTargetDummy extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage;
  private static List<Activity> activities = Arrays.asList(
    Activities.WALKING, Activities.STANDING, Activities.ATTACKING, Activities.STUNNED_SHORT
  );
  public EnemyTargetDummy(String name, Posn posn, Player player) {
    super(name, activities, posn, player);
    currentHP = maxHP = 100;
    minDamage = 5;
    maxDamage = 10;
  }

  @Override
  public void nextActivity() {
    super.nextActivity();
    /*if (currentActivity.equals("standing")) {
      for (BasicUnit u: game.getUnits()) {
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
