import java.util.Random;

import jwbgl.*;

public class EnemyRobedWizard extends RobedWizardUnit {
  private static String[] activities = {"walking", "standing", "falling", "teleporting", "appearing", "rezzing", "stunned_long"};
  
  /* These two percentages are additive */
  private double wanderChance = 0.025;
  private double teleportChance = 0.75;
  
  private int evasiveRadius = 3;
  private int teleportRadius = 6;
  private int visionRadius = 30;
  public EnemyRobedWizard(RPG game, String name, Posn posn, Player player) {
    super(game, name, activities, posn, player);
    currentHP = maxHP = 100;
    currentEP = maxEP = 200;
    teleportCost = 200;
  }
  
  @Override
  public void nextActivity() {
    super.nextActivity();
    Random RNG = game.getRNG();
    if (currentActivity.equals("standing")) {
      /* Set up variables for the flowchart. */
      boolean hostileInRange = false;
      Unit closestEnemy = null;
      Corpse closestCorpse = null;
      
      for (Unit u : game.getUnits()) {
        if (isHostile(u) && game.distance2(this, u) <= evasiveRadius) {
          hostileInRange = true;
          if (closestEnemy == null || game.distance2(this, u) < game.distance2(this, closestEnemy)) {
            closestEnemy = u;
          }
        }
      }
      if (!hostileInRange) {
        for (GameObject c : game.getObjects()) {
          if (c.isCorpse()) {
            if (closestCorpse == null || game.distance2(this, c) <= game.distance2(this, closestCorpse)) {
              closestCorpse = (Corpse) c;
            }
          }
        }
      }
      /* Execute flowchart. */
      /* Is there an enemy (player) unit in the danger zone? Teleport or walk away. */
      if (hostileInRange) {
        int x,y;
        Posn p;
        boolean goodPosn = false;
        int moveRadius;
        if (currentEP >= teleportCost) {
          setNextActivity("teleporting");
          moveRadius = teleportRadius;
        } else {
          setNextActivity("walking");
          moveRadius = 3;
        }
        do {
          /* The logic here is a little simpler than the Python version.
           * I'm also not sure it's bug-free... */
          x = RNG.nextInt(game.getFloor().getWidth());
          y = RNG.nextInt(game.getFloor().getHeight());
          p = new Posn(x,y);
          if (!game.getFloor().getTile(x,y).isBlocked()) {
            if (game.distance2(getPosn(), p) <= moveRadius) {
              if (game.distance2(p, closestEnemy.getPosn()) >= evasiveRadius) {
                goodPosn = true;
              }
            }
          }
        } while (!goodPosn);
        setNextTargetPosn(p);

      } else if (closestCorpse != null) {
        if (closestCorpse.getPosn().equals(getPosn())) {
          setNextActivity("rezzing");
        } else if (game.distance2(this, closestCorpse) <= visionRadius){
          setNextActivity("walking");
          setNextTargetPosn(closestCorpse.getPosn());
        }
      } else {
        double r = RNG.nextDouble();
        if (r < wanderChance) {
          /* Full wander. */
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
  }

  @Override
  public void playHitSound() {
    // TODO Auto-generated method stub
  }

  @Override
  public void playBashSound() {
    // TODO Auto-generated method stub
  }
  
  public void takeBashHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX()+dx, getY()+dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      /* Do we want to take partial damage? Do we want to block adjacent angles? */
    } else {
      setCurrentActivity("stunned_long");
      clearTargets();
      takeDamage(dmg);
    }
  }
  
  public void takeDamage(int dmg) {
    if (getCurrentActivity().equals("stunned_long")) {
      super.takeDamage(3*dmg);
    } else {
      super.takeDamage(dmg);
    }
  }
}
