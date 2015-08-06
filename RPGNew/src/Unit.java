import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import jwbgl.*;

/* Class representing any type of unit.  This class should be extended by any
 * subsequent unit classes we define.  Not to be used by itself (abstract). */
 
 /* ===== CHANGELOG =====
  * 6/6     - removed Unit interface, renamed this class from BasicUnit to Unit
  * 6/2-6/5 - worked on various activity sequencing (esp. walking), cleaned up
  *         - & removed counterintuitive helper methods
  * 5/30    - many comments.
  * 5/29    - worked on attacking & pathing.
  * 5/26    - Worked on pathing AI (esp. for multiple player units).
  *         - Moved most of the walk pathing code to doWalkPathing().
  * 5/25    - Added handling for midpath unit collisions.
  * ===================== */
public abstract class Unit extends BasicObject implements GameObject, Serializable {
  protected String name;
  protected String animationName;
  protected Hashtable<String, Surface> frames;
  protected ArrayList<Animation> animations;
  protected int dx;
  protected int dy;
  protected Unit targetUnit;
  protected Unit nextTargetUnit;
  protected Posn targetPosn;
  protected Posn nextTargetPosn;
  protected String[] activities;
  protected String currentActivity;
  protected String nextActivity;
  protected int currentHP, maxHP;
  protected int currentEP, maxEP;
  protected int hpBarOffset;
  protected boolean newSlashDirection;
  
  private Animation currentAnimation;
  private Player player;
  private FloorOverlay floorOverlay;
  private FloorOverlay targetPosnOverlay;
  private TransHealthBar healthBar;
  
  
  protected LinkedList<Posn> path;
  protected Hashtable<String, Accessory> equipment;
  protected Hashtable<Color, Color> paletteSwaps;
  
  protected int blockCost = 2; // costs N EP per tick (does not disable HP regen)
  protected int attackCost = 15; // 10 frames per attack for 67% uptime
  protected int bashCost = 36; // 12 frames per bash for 33% uptime
  protected int slashCost = 3; // costs N EP per tick (does not disable HP regen) // SHOULD BE 2
  protected int teleportCost = 200; // it's weird to put this in the base class but yeah.
  protected int hpRegen = 20; // regen 1 HP per N ticks
  protected int epRegen = 1; // regen 1 EP per N ticks
  
  public Unit(RPG game, String name, String animationName, String[] activities, Hashtable<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    super(game, posn);
    this.name = name;
    this.animationName = animationName;
    this.activities = activities;
    this.player = player;
    equipment = new Hashtable<String, Accessory>();
    this.paletteSwaps = paletteSwaps;
    
    dx = 0;
    dy = -1;
    xOffset = 0;
    /* Sprites are 80x80 (scaled), tiles are 96x48 (scaled).  There's an 8 pixel space 
     * at bottom of player sprites. */
    yOffset = -32;
    frames = new Hashtable<String, Surface>();
    loadAnimations();
    applyPaletteSwaps();
    setCurrentActivity("standing");
    //player.getUnits().add(this); // NO, the game will do this.
    updateFloorOverlay();
    healthBar = new TransHealthBar(this, 48, 12);
    hpBarOffset = -20;
    newSlashDirection = false;
    //System.out.printf("%s has %d frames\n",getClass(), frames.keySet().size());
  }
  
  public void applyPaletteSwaps() {
    // TODO Auto-generated method stub
    for (Animation anim: animations) {
      for (Surface s: anim.getFrames()) {
        s.setPaletteSwaps(paletteSwaps);
        s.applyPaletteSwaps();
      }
    }
  }

  // Face the unit toward the specified point.
  // That is, make <dx,dy> into an integer unit vector.
  // We're using Pythagorean distance rather than Civ distance here,
  // should we change?  
  public void pointAt(int x, int y) {
    int dx = x - this.getX();
    int dy = y - this.getY();
    double distance = Math.sqrt(dx*dx + dy*dy);
    if (distance != 0) {
      this.dx = (int) Math.round(dx/distance);
      this.dy = (int) Math.round(dy/distance);
    }
  }
  
  public void pointAt(Posn posn) {
    pointAt(posn.getX(), posn.getY());
  }
  
  public void pointAt(GameObject target) {
    pointAt(target.getX(), target.getY());
  }

  public void loadAnimations() {
    // we could easily rewrite this without k
    long t = System.currentTimeMillis();
    animations = new ArrayList<Animation>();
    for (int i = 0; i < activities.length; i++) {
      loadActivityAnimations(activities[i]);
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
    t = System.currentTimeMillis() - t;
    System.out.printf("%s.loadAnimations(): %d ms\n", this.getClass(), t);
  }
  
  /* Extend this as needed for animations that have different versions for
   * different units. Eventually that will likely be all of them. */
  public void loadActivityAnimations(String activity) {
    if (activity.equals("falling")) {
      loadFallingAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  public void loadGenericAnimations(String activity, String[] filenames) {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String[] filenames2 = new String[filenames.length];
      for (int j=0; j<filenames.length; j++) {
        String activityName = filenames[j].split("_")[0];
        String frameNum = filenames[j].split("_")[1];
        filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, activityName, RPG.DIRECTIONS[i], frameNum); 
      }        
      animations.add(new Animation(animationName, filenames2, activity, RPG.DIRECTIONS[i], frames));
    }
  }
  
  public void loadGenericAnimations(String activity) {
    String[] filenames = AnimationTemplates.getTemplate(activity);
    loadGenericAnimations(activity, filenames);
  }
  
  
  /* Falling has a few variations; this is for human units I think.
   * Two directions, NE or S. */
  public void loadFallingAnimations() {
    for (int i=0; i<RPG.DIRECTIONS.length; i++) {
      String dir = RPG.DIRECTIONS[i];
      String[] filenames = AnimationTemplates.FALLING;
      String[] filenames2 = new String[filenames.length];
      if (dir.equals("N") || dir.equals("NE") || dir.equals("E") || dir.equals("SE")) {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "falling", "NE", animIndex);
        }
      } else {
        for (int j=0; j<filenames.length; j++) {
          String animIndex = filenames[j].split("_")[1];
          filenames2[j] = String.format("%s_%s_%s_%s.png", animationName, "falling", "S", animIndex);
        }
      }
      animations.add(new Animation(animationName, filenames2, "falling", dir, frames));
    }
  }

  public void nextFrame() {
    if (currentAnimation.hasNextFrame()) {
      currentAnimation.nextFrame();
      for (Accessory e: equipment.values()) {
        e.nextFrame();
      }
    } else {
      nextActivity();
    }
  }
  
  public void nextActivity() {
    if (currentActivity.equals("falling")) {
      game.queueRemoveUnit(this);
      die();
    } else if (currentActivity.equals("blocking_1")) {
      if (nextActivity != null && nextActivity.equals("blocking_2")) {
        pointAt(nextTargetPosn);
        setCurrentActivity("blocking_2");
      } else {
        setCurrentActivity("blocking_3");
      }
    } else if (currentActivity.equals("blocking_2")) {
      if (nextActivity == null) {
        setCurrentActivity("blocking_3");
      } else if (nextActivity.equals("blocking_2") && currentEP >= blockCost) {
        pointAt(nextTargetPosn);
        setCurrentActivity("blocking_2");
      } else {
        setCurrentActivity("blocking_3");
        //setNextActivity(null);
      }
    
    /* BEGIN SHAMELESS C/P */
    } else if (currentActivity.equals("slashing_1")) {
      if (nextActivity != null && nextActivity.equals("slashing_2")) {
        pointAt(nextTargetPosn);
        setCurrentActivity("slashing_2");
      } else {
        setCurrentActivity("slashing_3");
      }
    } else if (currentActivity.equals("slashing_2")) {
      if (nextActivity == null) {
        setCurrentActivity("slashing_3");
      } else if (nextActivity.equals("slashing_2") && currentEP >= slashCost) {
        pointAt(nextTargetPosn);
        setCurrentActivity("slashing_2");
      } else {
        setCurrentActivity("slashing_3");
        //setNextActivity(null);
      }
    /* END SHAMELESS C/P */
    } else if (currentActivity.equals("teleporting")) {
      //setPosn(getTargetPosn());
      moveTo(getTargetPosn());
      setCurrentActivity("appearing");
      setTargetPosn(null);
      currentEP -= teleportCost;
      
    /* If next activity is rezzing:
     * 1) Make sure we're standing on the corpse.
     * 2) Make sure there's a free adjacent square to put the zombie on.
     * ...
     */
    } else if (currentActivity.equals("rezzing")) {
      setCurrentActivity("standing");
      setTargetPosn(null);
      GameObject targetCorpse = null;
      for (GameObject o : game.getFloor().getTile(getPosn()).getObjects()) {
        if (o.isCorpse()) {
          boolean allTilesBlocked = true;
          targetCorpse = o;
          for (int i=0; i<8; i++) {
            Posn p = game.getAdjacentSquares(getPosn()).get(i);
            if (!game.getFloor().getTile(p).isBlocked()) {
              allTilesBlocked = false;
            }
          }
          if (!allTilesBlocked) {
            Posn p;
            do {
              p = game.getAdjacentSquares(getPosn()).get(game.getRNG().nextInt(8));
            } while (game.getFloor().getTile(p).isBlocked());
            game.queueAddUnit(new EnemyZombie(game, String.format("Zombie %d", game.nextEnemyID()), p, game.getPlayer(2)));
          } else {
            System.out.println("help");
            /* anything? */ 
          }
        }
      }
      if (targetCorpse != null) {
        game.removeObject(targetCorpse);
      }
    } else if (nextActivity != null) {
      Unit lastTargetUnit = null;
      if (targetUnit != null) {
        lastTargetUnit = targetUnit;
      }
      setTargetPosn(nextTargetPosn);
      setTargetUnit(nextTargetUnit);
      if (lastTargetUnit != null) lastTargetUnit.updateFloorOverlay();
      if (nextActivity.equals("walking")) {
        if (getPosn().equals(targetPosn)) {
          setCurrentActivity("standing");
          targetPosn = null;
          nextActivity = null;
          setNextTargetPosn(null);
        } else {
          setPath(game.findPath(getPosn(), targetPosn));
          
          /* The pathing algorithm is configured to ignore units that are currently moving.
           * This is for the case where the first tile in the path contains a moving unit. */
          if (game.getFloor().getTile(path.peekFirst()).isBlocked()) {
            // System.out.println("fux");
          } else {
            pointAt(path.peekFirst());
            setCurrentActivity("walking");
            nextActivity = null;
            setNextTargetPosn(null);
            setNextTargetUnit(null);
          }
        }
      /* If next activity is attacking, we might have to path to the unit first. */
      } else if (nextActivity.equals("attacking")) {
        if (game.distance2(this, targetUnit) == 1) {
          if (currentEP >= attackCost) {
            pointAt(targetUnit);
            setCurrentActivity("attacking");
            nextActivity = null;
            setNextTargetPosn(null);
            setNextTargetUnit(null);
            currentEP -= attackCost;
          } else {
            setCurrentActivity("standing");
            targetPosn = null;
            nextActivity = null;
            setNextTargetPosn(null);
          }
        } else {
          setTargetPosn(targetUnit.getPosn());
          setPath(game.findPath(getPosn(), targetPosn));
          pointAt(path.peekFirst());
          setCurrentActivity("walking");
          /* Don't clear nextActivity/nextTargetUnit */ 
        }
      } else if (nextActivity.equals("bashing")) {
        if (game.distance2(this, targetUnit) == 1) {
          if (currentEP >= bashCost) {
            pointAt(targetUnit);
            setCurrentActivity("bashing");
            nextActivity = null;
            setNextTargetPosn(null);
            setNextTargetUnit(null);
            currentEP -= bashCost;
          } else {
            setCurrentActivity("standing");
            targetPosn = null;
            nextActivity = null;
            setNextTargetPosn(null);
          }
        } else {
          setTargetPosn(targetUnit.getPosn());
          setPath(game.findPath(getPosn(), targetPosn));

          if (game.getFloor().getTile(path.peekFirst()).isBlocked()) {
            // System.out.println("fux2");
          } else {
            pointAt(path.peekFirst());          
            setCurrentActivity("walking");
          }
          /* Don't clear nextActivity/nextTargetUnit */ 
        }
      } else if (nextActivity.equals("blocking_1")) {
        setTargetPosn(getNextTargetPosn());
        pointAt(targetPosn);
        setCurrentActivity("blocking_1");
        //System.out.printf("startblock - %s - %s\n", getPosn(), targetPosn);
        setNextActivity("blocking_2");
      } else if (nextActivity.equals("slashing_1")) {
        setTargetPosn(getNextTargetPosn());
        pointAt(targetPosn);
        setCurrentActivity("slashing_1");
        setNextActivity("slashing_2");
      } else if (nextActivity.equals("teleporting")) {
        setTargetPosn(getNextTargetPosn());
        setCurrentActivity("teleporting");
        setNextActivity(null);
        setNextTargetPosn(null);
      } else if (nextActivity.equals("rezzing")) {
        setCurrentActivity("rezzing");
        setNextActivity(null);
      }
      /* Shouldn't need to do this anymore now that setters do it for us automatically. 
      if (targetUnit != null) {
        targetUnit.updateFloorOverlay();
      }*/
    /* no nextactivity */
    } else { // if (nextActivity == null) {
      if (currentActivity.equals("walking")) {
        refreshWalk();
      } else {
        setCurrentActivity("standing");
        setTargetUnit(null);
      }
    }
  }

  /* This is the code that loops the walking! */
  public void refreshWalk() {
    /* This is the case where the next tile is non-empty but we're only
     * pausing for one turn. */ 
    if (targetPosn == null) {
      // System.out.println("Fuck!!!");
      //clearTargets();
      
    /* These two cases basically represent the same thing: we've arrived, or
     * we're otherwise out of path for some reason. */ 
    } else if (path == null || path.size() == 0) {
      setCurrentActivity("standing");
      clearTargets();
    } else if (getPosn().equals(targetPosn)) {
      setCurrentActivity("standing");
      clearTargets();
    /* Proceed. */
    } else {
      /* We're NOT calling checkNextTile here. It's OK, we'll check it before doing anything else
       * in doEvents() or something. */
      if (game.getFloor().getTile(path.peekFirst()).isBlocked()) {
        //System.out.println("fux4");
      } else {
        pointAt(path.peekFirst());
        setCurrentActivity("walking");
      }
    }
  }

  public boolean isHostile(Unit u) {
    return getPlayer().isHostile(u.getPlayer());
  }

  public void doUpkeep() {
    // somewhat confusingly, this is executed AFTER drawing
    // ... or is it? not anymore I don't think
    if (getCurrentActivity().equals("blocking_2")) {
      currentEP -= blockCost;
    } else if (getCurrentActivity().equals("slashing_2")) {
      currentEP -= slashCost;
    }
    if (!getCurrentActivity().equals("falling")) {
      if ((game.getTicks() % hpRegen == 0) && (currentHP < maxHP)) {
        currentHP++;
      }
      if ((game.getTicks() % epRegen == 0) && (currentEP < maxEP)) {
        currentEP++;
      }
    }
    if (currentEP<0) currentEP=0;
    this.nextFrame();
    //this.doManaRegen();
    //this.doHealthRegen();
  }
  
  public void clearTargets() {
    setTargetPosn(null);
    setTargetUnit(null);
    setNextTargetPosn(null);
    setNextTargetUnit(null);
    nextActivity = null;
    setTargetPosnOverlay(null);
  }

  /* So this is an important method.
   * It's a big mess.
   * The idea here is to handle ALL the activities for ALL unit types here,
   * so we won't have to copy and paste it into all the unit subclasses.
   * (They will each have their own nextActivity() logic.)
   * If this breaks down, figure out a way to split this method up. */
  public void doEvents() {
    if (getCurrentActivity().equals("walking")) {
      if (getCurrentAnimation().getIndex() <= 2) {
        checkNextTile();
      }
      if (getCurrentActivity().equals("walking")) {  
        if (getCurrentAnimation().getIndex() == 2) {
          move(dx, dy); // this is problematic for depth reasons.
          path.removeFirst();
        }
      }
    }
    /* This SEEMS redundant but checkNextTile() can change our activity to standing. */
    if (getCurrentActivity().equals("attacking")) {
      if (getCurrentAnimation().getIndex() == 2) {
        // What happens if the unit has moved away?
        Posn nextPosn = new Posn(getX()+dx, getY()+dy);
        
        if (targetUnit.getPosn().equals(nextPosn)) {
          doAttackHit(targetUnit);
        }
      }
    } else if (getCurrentActivity().equals("bashing")) {
      if (getCurrentAnimation().getIndex() == 0) {
        if (game.distance2(this, targetUnit) > 1) {
          setPath(game.findPath(this, targetUnit));
          if (path != null && path.size() > 0) {
            targetPosn = targetUnit.getPosn();
            if (game.getFloor().getTile(path.peekFirst()).isBlocked()) {
              // System.out.println("fux5");
            } else {
              setCurrentActivity("walking");
            }
          } else {
            setCurrentActivity("standing");
            clearTargets();
            System.out.println("uh oh");
          }
        }
      } else if (getCurrentAnimation().getIndex() == 2) {
        
        // What happens if the unit has moved away?
        Posn nextPosn = new Posn(getX()+dx, getY()+dy);
        if (targetUnit != null && targetUnit.getPosn().equals(nextPosn)) {
          doBashHit(targetUnit);
        } else {
        }
      }
    } else if (getCurrentActivity().equals("blocking_2")) {
      /* WTF? */
      if (currentHP == 0) {
        setNextActivity(null);
        setNextTargetPosn(null);
      }
    } else if (getCurrentActivity().equals("slashing_2")) {
      if (getCurrentAnimation().getIndex() == 0) {
        if (newSlashDirection) {
          newSlashDirection = false;
          Posn nextPosn = getPosn().add(getDirection());
          Unit tu = game.getFloor().getTile(nextPosn).getUnit();
          if (tu != null && isHostile(tu)) {
            doSlashHit(tu);
          }
        }
      }
    }
  }

  // deal with falling animations, sfx, etc later.
  public void die() {
    game.removeObject(getFloorOverlay());
    floorOverlay = null;
  }
  
  /* Does NOT validate the tile we're moving to. You have to do that yourself!
   * checkNextTile() was supposed to do that but it kind of grew in scope. */
  public void move(int dx, int dy) {
    //System.out.printf("move: %s %s\n", getPosn(), new Posn(getX() + dx, getY() + dy));
    game.getDepthTree().remove(this);
    Tile t = game.getFloor().getTile(getX(), getY());
    t.setUnit(null);
    
    setPosn(new Posn(getX() + dx, getY() + dy));
    updateDepth();
    game.getDepthTree().add(this);
    t = game.getFloor().getTile(getX(), getY());
    t.setUnit(this);
    updateFloorOverlay();
  }
  
  /* Moves to a posn rather than a relative amount. */
  public void moveTo(Posn p) {
    game.getDepthTree().remove(this);
    Tile t = game.getFloor().getTile(getX(), getY());
    t.setUnit(null);
    
    setPosn(p);
    updateDepth();
    game.getDepthTree().add(this);
    t = game.getFloor().getTile(getX(), getY());
    t.setUnit(this);
    updateFloorOverlay();
  }
  
  /* We can call this on any GameObject for differentiation purposes. */
  public boolean isUnit() {
    return true;
  }
  
  public String getName() {
    return name;
  }
  public String getNextActivity() {
    return nextActivity;
  }
  
  public String toString() {
    return "<Unit("+getName()+")>";
  }
  
  /* Possibly poorly named.  This function will look at the tile directly in front of the unit,
   * and do one of three things:
   * 1) Nothing, if the tile is open;
   *    - currentActivity = walking
   *    - nextActivity = null or attacking
   * 2) Cancel our movement, if there is something blocking us in the tile;
   *    - currentActivity = standing
   *    - nextActivity = null
   * 3) Wait for a turn, if there is something temporarily blocking us.
   *    - currentActivity = standing
   *    - nextActivity = walking or attacking
   *    
   * Note that it does NOT set currentActivity to walking directly.
   * 
   * There should always be a targetPosn, right? */ 
  public void checkNextTile() {
    Tile nextTile = game.getFloor().getTile(getX()+dx, getY()+dy); // should match path.first
    if (nextTile.isBlocked()) {
      // If our target posn is next in the path and it's blocked, cancel the movement.
      // Is this good?
      Unit blockingUnit = nextTile.getUnit();
      if (targetPosn != null && nextTile.getPosn().equals(targetPosn)) {
        // note that we're using setCurrentActivity, not setNextActivity
        
        // If there's a unit at the target position:
        // if it's walking, wait for it to move,
        // otherwise, cancel pathing.
        if (nextTile.getUnit() != null) {
          if (targetUnit != null && blockingUnit.equals(targetUnit)) {
            setNextTargetUnit(targetUnit);
            setNextActivity("attacking");
            setCurrentActivity("standing");
            setTargetUnit(null);
          } else if (blockingUnit.isMoving()) {
            /* Nothing queued up, just moving to a spot. */
            if (nextActivity == null || nextActivity.equals("walking")) {
              setCurrentActivity("standing");
              //System.out.println("W1");
              setNextActivity("walking");
              setNextTargetPosn(targetPosn);
              setTargetPosn(null);
            /* Attacking is queued up; keep it queued. */
            } else if (nextActivity.equals("attacking") || nextActivity.equals("blocking")) { 
              setCurrentActivity("standing");
            } else {
              System.out.println("Debug 1");
              printDebug();
            }
          } else { 
            /* There's a unit on our target posn and it's not moving: cancel pathing. */
            setCurrentActivity("standing");
            clearTargets();
          }
        // If the target posn is blocked by an object - maybe this would
        // happen with fog of war - stop pathing.
        } else {
          setCurrentActivity("standing");
          clearTargets();
        }

      /* If we're not next to the target posn and our next tile is
       * blocked, compute a new path.  This fails if the target tile is
       * perma-blocked - what do we do? */
      
      } else { // blocked and nextposn != targetposn
        blockingUnit = nextTile.getUnit();
        if (blockingUnit != null && blockingUnit.isMoving()) {
          /* Just walking, no unit target */
          if (nextActivity == null || nextActivity.equals("walking")) {
            setCurrentActivity("standing");
            setNextTargetPosn(targetPosn);
            setTargetPosn(null);
            setNextActivity("walking");
          } else if (nextActivity.equals("attacking") || nextActivity.equals("bashing")) {
            setCurrentActivity("standing");
            setTargetPosn(null);
          } else { 
            //printDebug();
          }
        } else {
          /* If the path is blocked by an object or a non-moving unit, better re-path around it. */
          
          /* Just walking, no unit target */
          if (nextActivity == null) {
            setCurrentActivity("standing");
            setNextTargetPosn(targetPosn);
            targetPosn = null;
            setNextActivity("walking");
            setPath(game.findPath(getPosn(), nextTargetPosn));
          } else if (nextActivity.equals("attacking") || nextActivity.equals("bashing")) {
            setCurrentActivity("standing");
            targetPosn = null;
            setPath(game.findPath(this, nextTargetUnit));
          } else {
            System.out.println("Debug 3");
          }
        }
      }
    }
  }

  // Stop walking, reset to standing, cancel walk.
  public void stopWalking() {
    setCurrentActivity("standing");
    clearTargets();
  }

  // Add the appropriate red/green highlight under the unit.
  // Eventually we'll want some other colors for neutral units, NPCs, etc.
  public void updateFloorOverlay() {
    if (floorOverlay != null) {
      game.getDepthTree().remove(floorOverlay);
      floorOverlay = null;
    }
    if (game.getHumanPlayer().isHostile(getPlayer())) {
      if (game.getPlayerUnit().getTargetUnit() == this) {
        Color transRed = new Color(255,0,0,64);
        floorOverlay = new FloorOverlay(game, this, Color.RED, transRed);
      } else {
        floorOverlay = new FloorOverlay(game, this, Color.RED);
      }
    } else {
      Color transGreen = new Color(0,255,0,64);
      floorOverlay = new FloorOverlay(game, this, Color.GREEN, transGreen);
      //floorOverlay = new FloorOverlay(game, this, Color.GREEN);
    }
    int oldSize = game.getDepthTree().size();
    game.getDepthTree().add(floorOverlay);
    int newSize = game.getDepthTree().size();
  }
  
  public boolean isMoving() {
    return currentActivity.equals("walking");
  }
  
  // ===== ACCESSOR METHODS =====
  
  public void setTargetUnit(Unit u) {
    Unit oldTargetUnit = targetUnit;
    targetUnit = u;
    if (oldTargetUnit != null) {
      oldTargetUnit.updateFloorOverlay();
    }
    if (u != null) {
      u.updateFloorOverlay();
    }
  }
  
  public Unit getTargetUnit() {
    return targetUnit;
  }
  
  public void setPath(LinkedList<Posn> path) {
    this.path = path;
  }

  // Important: this takes a Posn as an argument, not a FloorOverlay
  public void setTargetPosnOverlay(Posn posn) {
    if (targetPosnOverlay != null) {
      game.getDepthTree().remove(targetPosnOverlay);
      targetPosnOverlay = null;
    }
    if (posn != null) {
      //Color transCyan = new Color(0,255,255,64);
      //targetPosnOverlay = new FloorOverlay(game, game.getFloor().getTile(posn), Color.CYAN, transCyan);
      targetPosnOverlay = new FloorOverlay(game, game.getFloor().getTile(posn), Color.CYAN);
      game.getDepthTree().add(targetPosnOverlay);
    }
  }
  
  public void setTargetPosn(Posn p) {
    targetPosn = p;
  }
  
  public Surface getSurface() {
    return getCurrentAnimation().getCurrentFrame();
  }
  
  public Posn getTargetPosn() {
    return targetPosn;
  }
  
  public Animation getCurrentAnimation() {
    return currentAnimation;
  }
  
  public String getCurrentActivity() {
    return currentActivity;
  }
  
  public void setCurrentActivity(String newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  public void setCurrentAnimation(String activity, String direction) {
    int i = 0;
    Animation oldAnimation = currentAnimation;
    currentAnimation = null;
    while (i < animations.size()) {
      if (!animations.get(i).getActivity().equals(activity)) {
        i += 8;
      } else if (!animations.get(i).getDirection().equals(direction)) {
        i++;
      } else {
        // It's a match!
        currentAnimation = animations.get(i);
        currentAnimation.setIndex(0);
        break;
      }
    }
    if (currentAnimation == null) {
      System.out.printf("fuck, couldn't find animation: %s %s\n", activity, direction);
      currentAnimation = oldAnimation;
    }
    for (Accessory e : equipment.values()) {
      e.setCurrentAnimation(activity, direction);
    }
  }

  public String getAnimationName() {
    return animationName;
  }
  
  /* VERY confusing: this one returns "N", "NE" etc. while getDirection() uses coords. */
  public String getCurrentDirection() {
    return game.coordsToDir(dx, dy);
  }
  
  public Rect getRect() {
    Posn pixel = game.gridToPixel(getPosn()); // returns top left
    int left = pixel.getX() + RPG.TILE_WIDTH/2 - getSurface().getWidth()/2 + xOffset;
    int top = pixel.getY() + RPG.TILE_HEIGHT/2 - getSurface().getHeight()/2 + yOffset;
    Rect transparencyRect = getSurface().getTransparencyRect().clone();
    transparencyRect.move(left,top);
    return transparencyRect; 
  }
  
  public Player getPlayer() {
    return player;
  }

  // Overridden by damage mechanics?
  public void doAttackHit(Unit u) {
    int d = 1; // ...
    u.takeHit(this, d);
    playHitSound();
  }
  
  public abstract void playHitSound();
  public abstract void playBashSound();

  public void doBashHit(Unit u) {
    int d = 1; // ...
    u.takeHit(this, d);
  }
  
  public void doSlashHit(Unit u) {
    int d = 1; // ...
    u.takeHit(this, d);
    playHitSound();
    System.out.println("OMG SLASH HIT");
  }
  public void setNextTargetUnit(Unit u) {
    nextTargetUnit = u;
    if (u != null) {
      setTargetPosnOverlay(null);
    }
  }
  
  public void setNextTargetPosn(Posn p) {
    nextTargetPosn = p;
  }
  
  public void takeDamage(int dmg) {
    if (dmg >= currentHP) {
      currentHP = 0;
      if (!getCurrentActivity().equals("falling")) {
        setCurrentActivity("falling");
      }
    } else {
      currentHP -= dmg;
    }
  }
  
  /* Source is a GameObject, not a unit - allows for stuff like projectiles,
   * floor fire, etc. */
  public void takeHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX()+dx, getY()+dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      /* Do we want to take partial damage? Do we want to block adjacent angles? */
    } else {
      takeDamage(dmg);
    }
  }
  
  public void takeBashHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX()+dx, getY()+dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      /* Do we want to take partial damage? Do we want to block adjacent angles? */
    } else {
      setCurrentActivity("stunned_short");
      clearTargets();
      takeDamage(dmg);
    }
  }
  
  public void takeSlashHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX()+dx, getY()+dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      /* Do we want to take partial damage? Do we want to block adjacent angles? */
    } else {
      setCurrentActivity("standing");
      clearTargets();
      takeDamage(dmg);
    }
  }

  /* For the time being, we're treating the different blocking animations as
   * equivalent for mitigation purposes. Think about whether this is what
   * we want. */
  public boolean isBlocking() {
    if (getCurrentActivity().equals("blocking_1")) return true;
    else if (getCurrentActivity().equals("blocking_2")) return true;
    else if (getCurrentActivity().equals("blocking_3")) return true;
    else return false;
  }

  public int getCurrentHP() {
    return currentHP;
  }
  
  public int getMaxHP() {
    return maxHP;
  }
  
  public int getCurrentEP() {
    return currentEP;
  }
  
  public int getMaxEP() {
    return maxEP;
  }

  /* Perhaps confusing: doesn't return "NE" etc., but returns Posn<-1, -1> etc. */
  public Posn getDirection() {
    return new Posn(dx, dy);
  }
  
  public void setDirection(Posn p) {
    dx = p.getX();
    dy = p.getY();
  }
  
  public FloorOverlay getFloorOverlay() {
    return floorOverlay;
  }
  
  public void draw(Graphics g) {
    if (equipment.get("mainhand") != null) {
      if (equipment.get("mainhand").drawBehind() ) {
        equipment.get("mainhand").draw(g);
      }
    }
    if (equipment.get("offhand") != null) {
      if (equipment.get("offhand").drawBehind() ) {
        equipment.get("offhand").draw(g);
      }
    }
    super.draw(g);
    if (equipment.get("mainhand") != null) {
      if (!equipment.get("mainhand").drawBehind()) {
        equipment.get("mainhand").draw(g);
      }
    }
    if (equipment.get("offhand") != null) {
      if (!equipment.get("offhand").drawBehind() ) {
        equipment.get("offhand").draw(g);
      }
    }
    
    Posn pixel = game.gridToPixel(getPosn()); // returns top left
    int left = pixel.getX() + RPG.TILE_WIDTH/2 - healthBar.getWidth()/2 + xOffset;
    int top = pixel.getY() + RPG.TILE_HEIGHT/2 - healthBar.getHeight()/2 + yOffset;
    top += hpBarOffset;
    
    healthBar.draw(g, left, top);
  }
  
  public void addAccessory(Accessory e) {
    equipment.put(e.getSlot(), e);
    e.setCurrentAnimation(this.getCurrentActivity(), game.coordsToDir(dx,dy));
  }

  public void setNextActivity(String activity) {
    nextActivity = activity;
  }

  public Unit getNextTargetUnit() {
    return nextTargetUnit;
  }

  public Posn getNextTargetPosn() {
    return nextTargetPosn;
  }
  
  public void printDebug() {
    System.out.printf("<%s - %s %s %s %s %s %s>\n", getClass(), currentActivity, nextActivity, targetPosn, nextTargetPosn, targetUnit, nextTargetUnit);
  }

  public void setFloorOverlay(FloorOverlay floorOverlay) {
    this.floorOverlay = floorOverlay;
  }

  public FloorOverlay getTargetPosnOverlay() {
    return targetPosnOverlay;
  }
}
