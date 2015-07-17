import java.util.Random;

import jwbgl.*;

public class EnemyRobedWizard extends RobedWizardUnit {
  private static String[] activities = {"walking", "standing", "falling", "teleporting", "appearing"};
  private int teleportCost = 200;
  private double moveChance = 0.25;
  public EnemyRobedWizard(RPG game, String name, Posn posn, Player player) {
    super(game, name, activities, posn, player);
    currentHP = maxHP = 100;
    currentEP = maxEP = 200;
    System.out.println("hihi.");
  }
  
  @Override
  public void nextActivity() {
    printDebug();
    super.nextActivity();
    if (currentActivity.equals("standing")) {
      Random RNG = game.getRNG();
      if (RNG.nextDouble() < moveChance) {
        int x,y;
        do {
          x = RNG.nextInt(game.getFloor().getWidth());
          y = RNG.nextInt(game.getFloor().getHeight());
        } while (game.getFloor().getTile(x,y).isBlocked());
        setNextTargetPosn(new Posn(x,y));
        setNextActivity("walking");
      }
    }
  }

  @Override
  public void playHitSound() {
    // TODO Auto-generated method stub
  }

  @Override
  public void playBashSound() {
    // TODO Auto-generated method stub
  }
}
