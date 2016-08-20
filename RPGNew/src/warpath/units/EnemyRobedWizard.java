package warpath.units;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jwbgl.*;
import warpath.activities.ActivityNames;
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.objects.Corpse;
import warpath.objects.GameObject;
import warpath.players.Player;

/**
 * Represents the first type of robed wizard enemy, with behavior including
 * teleporting and zombie-resurrecting.
 * TODO The value of teleport_chance is not used.
 */
public class EnemyRobedWizard extends RobedWizardUnit {
  private static final long serialVersionUID = 1L;

  private static final List<String> ACTIVITIES = Arrays.asList(
    ActivityNames.APPEARING, ActivityNames.FALLING, ActivityNames.REZZING, ActivityNames.STANDING, ActivityNames.STUNNED_SHORT,
    ActivityNames.STUNNED_LONG, ActivityNames.TELEPORTING, ActivityNames.WALKING
  );
  
  // These two percentages are additive
  private static final double WANDER_CHANCE = 0.025;
  private static final double TELEPORT_CHANCE = 0.75;
  
  private static final int EVASIVE_RADIUS = 3;
  private static final int TELEPORT_RADIUS = 6;
  private static final int VISION_RADIUS = 30;
  
  public EnemyRobedWizard(String name, Posn posn, Player player) {
    super(name, ACTIVITIES, posn, player);
    currentHP = maxHP = 200;
    currentEP = maxEP = 200;
  }
  
  @Override
  public void nextActivity() {
    super.nextActivity();
    RPG game = RPG.getInstance();
    Random RNG = game.getRNG();
    if (currentActivity.equals(ActivityNames.STANDING)) {
      // Set up variables for the flowchart.
      boolean hostileInRange = false;
      BasicUnit closestEnemy = null;
      Corpse closestCorpse = null;
      
      for (BasicUnit u : game.getUnits()) {
        if (isHostile(u) && Utils.distance2(this, u) <= EVASIVE_RADIUS) {
          hostileInRange = true;
          if (closestEnemy == null || Utils.distance2(this, u) < Utils.distance2(this, closestEnemy)) {
            closestEnemy = u;
          }
        }
      }
      if (!hostileInRange) {
        for (GameObject c : game.getObjects()) {
          if (c.isCorpse()) {
            if (closestCorpse == null || Utils.distance2(this, c) < Utils.distance2(this, closestCorpse)) {
              if (!game.getFloor().getTile(c.getPosn()).isBlocked() || c.getPosn().equals(getPosn())) {
                closestCorpse = (Corpse) c;
              }
            }
          }
        }
      }
      // Execute flowchart.
      // Are we standing on a corpse? Start rezzing, regardless of threats.
      if ((closestCorpse != null) && (Utils.distance2(this, closestCorpse) <= VISION_RADIUS) && (closestCorpse.getPosn().equals(getPosn()))) {
        setNextActivity(ActivityNames.REZZING);
        // Is there an enemy (player) unit in the danger zone? Teleport or walk away.
      } else if (hostileInRange) {
        int x,y;
        Posn p;
        boolean goodPosn = false;
        int moveRadius;
        if (currentEP >= TELEPORT_COST) {
          setNextActivity(ActivityNames.TELEPORTING);
          moveRadius = TELEPORT_RADIUS;
        } else {
          setNextActivity(ActivityNames.WALKING);
          moveRadius = 3;
        }
        // Find a tile to teleport to.
        do {
          // The logic here is a little simpler than the Python version.
          // I'm also not sure it's bug-free...
          x = RNG.nextInt(game.getFloor().getWidth());
          y = RNG.nextInt(game.getFloor().getHeight());
          p = new Posn(x,y);
          if (!game.getFloor().getTile(x,y).isBlocked()) {
            if (Utils.distance2(getPosn(), p) <= moveRadius) {
              if (Utils.distance2(p, closestEnemy.getPosn()) >= EVASIVE_RADIUS) {
                goodPosn = true;
              }
            }
          }
        } while (!goodPosn);
        setNextTargetPosn(p);
      } else if ((closestCorpse != null) && (Utils.distance2(this, closestCorpse) <= VISION_RADIUS)) {
        setNextActivity(ActivityNames.WALKING);
        setNextTargetPosn(closestCorpse.getPosn());
      } else {
        double r = RNG.nextDouble();
        if (r < WANDER_CHANCE) {
          // Full wander.
          int x,y;
          do {
            x = RNG.nextInt(game.getFloor().getWidth());
            y = RNG.nextInt(game.getFloor().getHeight());
          } while (game.getFloor().getTile(x,y).isBlocked());
          setNextTargetPosn(new Posn(x,y));
          setNextActivity(ActivityNames.WALKING);
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
    Posn blockedPosn = new Posn(getX() + dx, getY() + dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      // Do we want to take partial damage? Do we want to block adjacent angles?
    } else {
      // IMPORTANT: take the damage BEFORE changing the activity, since we don't want to take
      // the multiplied damage on this hit.
      takeDamage(dmg);
      if (getCurrentActivity().equals(ActivityNames.REZZING)) {
        setCurrentActivity(ActivityNames.STUNNED_LONG);
      } else {
        setCurrentActivity(ActivityNames.STUNNED_SHORT);
      }
      clearTargets();
    }
  }
  
  public void takeDamage(int dmg) {
    if (getCurrentActivity().equals(ActivityNames.STUNNED_LONG)) {
      super.takeDamage(3*dmg);
    } else {
      super.takeDamage(dmg);
    }
  }
}
