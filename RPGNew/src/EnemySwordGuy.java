import java.util.Random;

/* A basic enemy unit for testing! */

public class EnemySwordGuy extends HumanUnit {
  private int minDamage, maxDamage;
  public EnemySwordGuy(RPG game, String name, Posn posn, Player player) {
    super(game, name, posn, player);
    currentHP = maxHP = 100;
    minDamage = 5;
    maxDamage = 10;
  }

  public void nextActivity() {
    super.nextActivity();
    if (currentActivity.equals("standing")) {
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
    }
  }
}
