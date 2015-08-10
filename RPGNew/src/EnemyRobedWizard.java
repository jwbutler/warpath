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
    currentHP = maxHP = 200;
    currentEP = maxEP = 200;
    teleportCost = 200;
    rezCost = 200;
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
            if (closestCorpse == null || game.distance2(this, c) < game.distance2(this, closestCorpse)) {
              if (!game.getFloor().getTile(c.getPosn()).isBlocked() || c.getPosn().equals(getPosn())) {
                closestCorpse = (Corpse) c;
              }
            }
          }
        }
      }
      /* Execute flowchart. */
      /* Are we standing on a corpse? Start rezzing, regardless of threats. */
      if ((closestCorpse != null) && (game.distance2(this, closestCorpse) <= visionRadius) && (closestCorpse.getPosn().equals(getPosn()))) {
        setNextActivity("rezzing");
        /* Is there an enemy (player) unit in the danger zone? Teleport or walk away. */
      } else if (hostileInRange) {
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
        /* Find a tile to teleport to. */
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
      } else if ((closestCorpse != null) && (game.distance2(this, closestCorpse) <= visionRadius)) {
        setNextActivity("walking");
        setNextTargetPosn(closestCorpse.getPosn());
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
      /* IMPORTANT: take the damage BEFORE changing the activity, since we don't want to take
       * the multiplied damage on this hit. */
      takeDamage(dmg);
      if (getCurrentActivity().equals("rezzing")) {
        setCurrentActivity("stunned_long");
      } else {
        setCurrentActivity("stunned_short");
      }
      clearTargets();
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
