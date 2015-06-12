import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
  private String name;
  protected String animationName;
  private Animation[] animations;
  protected int dx;
  protected int dy;
  private int xOffset;
  private int yOffset;
  protected Unit targetUnit;
  protected Unit nextTargetUnit;
  protected Posn targetPosn;
  protected Posn nextTargetPosn;
  private String[] activities;
  protected String currentActivity;
  protected String nextActivity;
  private Animation currentAnimation;
  private Player player;
  protected LinkedList<Posn> path;
  private FloorOverlay floorOverlay;

  private FloorOverlay targetPosnOverlay;
  protected int currentHP;
  protected int maxHP;
  
  private TransHealthBar healthBar;
  protected HashMap<String, Accessory> equipment;
  protected HashMap<Color, Color> paletteSwaps;
  
  public Unit(RPG game, String name, String animationName, String[] activities, HashMap<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    super(game, posn);
    this.name = name;
    this.animationName = animationName;
    this.activities = activities;
    this.player = player;
    equipment = new HashMap<String, Accessory>();
    this.paletteSwaps = paletteSwaps;
    
    dx = 0;
    dy = -1;
    xOffset = 0;
    /* Sprites are 80x80 (scaled), tiles are 96x48.  There's an 8 pixel space 
     * at bottom of player sprites. */
    yOffset = -32;
    loadAnimations();
    applyPaletteSwaps();
    setCurrentActivity("standing");
    //player.getUnits().add(this); // NO, the game will do this.
    updateFloorOverlay();
    healthBar = new TransHealthBar(this, 48, 12);
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

  public void endAttack() {
    // TODO Auto-generated method stub
    
  }
 
  // 
  public void loadAnimations() {
    // we could easily rewrite this without k

    animations = new Animation[activities.length * RPG.DIRECTIONS.length];
    int k = 0;
    for (int i = 0; i < activities.length; i++) {
      for (int j = 0; j < RPG.DIRECTIONS.length; j++) {
        String activity = activities[i];
        String[] filenames = AnimationTemplates.getTemplate(activity);
        animations[k++] = new Animation(animationName, filenames, activity, RPG.DIRECTIONS[j]);
      }
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
  }
  
  //
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
    if (nextActivity != null) {
      if (targetUnit != null) {
        targetUnit.updateFloorOverlay();
      }
      setTargetPosn(nextTargetPosn);
      setTargetUnit(nextTargetUnit);
      /* If next activity is walking, do pathfinding. */
      if (nextActivity.equals("walking")) {
        setPath(game.findPath(getPosn(), targetPosn));
        pointAt(path.peekFirst());
        setCurrentActivity(nextActivity);
        nextActivity = null;
        nextTargetPosn = null; 
        nextTargetUnit = null;
      /* If next activity is attacking, we might have to path to the unit first. */
      } else if (nextActivity.equals("attacking")) {
        if (game.distance(this, targetUnit) == 1) {
          pointAt(targetUnit);
          setCurrentActivity("attacking");
          nextActivity = null;
          nextTargetPosn = null;
          nextTargetUnit = null;
        } else {
          setTargetPosn(targetUnit.getPosn());
          setPath(game.findPath(getPosn(), targetPosn));
          pointAt(path.peekFirst());
          setCurrentActivity("walking");
          /* Don't clear nextActivity/nextTargetUnit */ 
        }
      } else if (nextActivity.equals("bashing")) {
        if (game.distance(this, targetUnit) == 1) {
          pointAt(targetUnit);
          setCurrentActivity("bashing");
          nextActivity = null;
          nextTargetPosn = null;
          nextTargetUnit = null;
        } else {
          setTargetPosn(targetUnit.getPosn());
          setPath(game.findPath(getPosn(), targetPosn));
          pointAt(path.peekFirst());
          setCurrentActivity("walking");
          /* Don't clear nextActivity/nextTargetUnit */ 
        }
      }
      if (targetUnit != null) {
        targetUnit.updateFloorOverlay();
      }
    /* no nextactivity */
    } else if (nextActivity == null) {
      if (currentActivity.equals("walking")) {
        refreshWalk();
      } else {
        setCurrentActivity("standing");
      }
    }
  }
  
  // Load the next activity; for example, in the case of walking along a path,
  // we'll be issuing a walk order after each movement.
  // I'm not sure this is the right place to issue repeated attack orders.
  public void nextActivityOLD() {
    //System.out.printf("<%s - %s %s %s %s %s %s>\n", getClass(), currentActivity, nextActivity, targetPosn, nextTargetPosn, targetUnit, nextTargetUnit);
    if (nextTargetUnit != null) {
      if (targetUnit != null) targetUnit.updateFloorOverlay();
      targetUnit = nextTargetUnit;
      targetPosn = targetUnit.getPosn();
      setTargetPosnOverlay(null); // check this
      nextTargetUnit = null;
      nextTargetPosn = null;
      targetUnit.updateFloorOverlay();
      
    } else if (nextTargetPosn != null) {
      if (nextActivity == null) {
        System.out.println("next activity is null. fix this, idiot (current="+currentActivity+")");
      } else if (nextActivity.equals("walking")) {
        targetPosn = nextTargetPosn;
        setPath(game.findPath(getPosn(), targetPosn));
        if (targetUnit != null) {
          Unit u = targetUnit;
          targetUnit = null;
          u.updateFloorOverlay();
        }
        setCurrentActivity("walking");
        nextTargetPosn = null;
        nextActivity = null;
      } else if (nextActivity.equals("blocking_1")) {
        setCurrentActivity("blocking_1");
        targetPosn = nextTargetPosn;
        nextTargetPosn = null;
        nextActivity = null;
      }
    } else if (currentActivity.equals("attacking")) {
      setCurrentActivity("standing");
      clearTargets();
      return;
    } else if (currentActivity.equals("bashing")) {
      setCurrentActivity("standing");
      clearTargets();
      return;
    } else if (currentActivity.equals("blocking_1") || currentActivity.equals("blocking_2")) {
      if (game.ctrlIsDown()) {
        setTargetPosn(game.pixelToGrid(game.getMousePosn()));
        pointAt(targetPosn);
        setCurrentActivity("blocking_2");
        clearTargets();
        return;      
      } else {
        setTargetPosn(game.pixelToGrid(game.getMousePosn()));
        pointAt(targetPosn);
        setCurrentActivity("blocking_3");
        clearTargets();
        return;
      }
    } else if (currentActivity == "stunned_short") {
      System.out.println("end stun");
      setCurrentActivity("standing");
      clearTargets();
      return;
    }
    if (targetUnit != null) {
      if (isHostile(targetUnit)) {
        if (game.distance(this, targetUnit) <= 1) {
          pointAt(targetUnit);
          if (nextActivity == null) {
            System.out.println("fix null activity idiot, current = " + currentActivity);
            System.out.println("instanceof enemyunit? "+(this instanceof EnemySwordGuy));
            return;
          }
          if (nextActivity.equals("attacking") || nextActivity.equals("bashing")) {
            setCurrentActivity(nextActivity);
            setNextActivity(null);
          }
          return;
        } else {
          setCurrentActivity("walking");
          setPath(game.findPath(this, targetUnit));
          // walking code below
        }
      }
    }

    if (targetPosn != null) {
      if (currentActivity.equals("walking")) {
        /* Queue up another walking animation.  Includes checks for
         * whether we've reached destination, etc. */
        refreshWalk();
      } else if (currentActivity.equals("blocking_1")) {
        setTargetPosn(game.pixelToGrid(game.getMousePosn()));
        pointAt(targetPosn);
      }
    } else {
      if (game.ctrlIsDown()) {
        setTargetPosn(game.pixelToGrid(game.getMousePosn()));
        pointAt(targetPosn);
        setCurrentActivity("blocking_1");
      } else {
        System.out.println("hmm.");
        setCurrentActivity("standing");
        clearTargets();
      }
    }
  }

  /* This is the code that loops the walking! */
  private void refreshWalk() {
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
      pointAt(path.peekFirst());
      setCurrentActivity("walking");
    }
  }

  public boolean isHostile(Unit u) {
    return getPlayer().isHostile(u.getPlayer());
  }

  public void doUpkeep() {
    // somewhat confusingly, this is executed AFTER drawing
    // ... or is it? not anymore I don't think
    this.nextFrame();
    //this.doManaRegen();
    //this.doHealthRegen();
  }
  
  public void clearTargets() {
    targetPosn = null;
    targetUnit = null;
    nextTargetPosn = null;
    nextTargetUnit = null;
    nextActivity = null;
    if (targetPosnOverlay != null) {
      game.getDepthTree().remove(targetPosnOverlay);
      targetPosnOverlay = null;
    }
  }

  public void doEvents() {
    //System.out.println(getCurrentActivity() + " " + nextActivity);
    if (getCurrentActivity().equals("walking")) {
      if (getCurrentAnimation().getIndex() <= 2) {
        checkNextTile();
      }
      
      if (getCurrentAnimation().getIndex() == 2) {
        move(dx, dy); // this is problematic for depth reasons.
        path.removeFirst();
      }
    } else if (getCurrentActivity().equals("attacking")) {
      if (getCurrentAnimation().getIndex() == 2) {
        // What happens if the unit has moved away?
        Posn nextPosn = new Posn(getX()+dx, getY()+dy);
        
        /* Why are we sometimes losing targetUnit?
         * Haven't seen this one in a while thankfully. */
        if (targetUnit == null) {
          System.out.println("FIX TARGETING PROBLEM IDIOT");
          return;
        }
        
        //System.out.println(targetUnit + " " + nextPosn);
        if (targetUnit.getPosn().equals(nextPosn)) {
          doAttackHit(targetUnit);
        } else {
          //System.out.println("Missed");
        }
      }
    } else if (getCurrentActivity().equals("bashing")) {
      if (getCurrentAnimation().getIndex() == 0) {
        if (game.distance(this, targetUnit) > 1) {
          setPath(game.findPath(this, targetUnit));
          if (path != null && path.size() > 0) {
            targetPosn = targetUnit.getPosn();
            setCurrentActivity("walking");
          } else {
            setCurrentActivity("standing");
            clearTargets();
            System.out.println("uh oh");
          }
        }
      } else if (getCurrentAnimation().getIndex() == 2) {
        // What happens if the unit has moved away?
        Posn nextPosn = new Posn(getX()+dx, getY()+dy);
        
        /* Why are we sometimes losing targetUnit?
         * Haven't seen this one in a while thankfully. */
        if (targetUnit == null) {
          System.out.println("FIX TARGETING PROBLEM IDIOT");
          return;
        }
        
        //System.out.println(targetUnit + " " + nextPosn);
        if (targetUnit.getPosn().equals(nextPosn)) {
          doBashHit(targetUnit);
        } else {
          //System.out.println("Missed");
        }
      }
    }
  }
  
  public void die() {
    // deal with falling animations, sfx, etc later.
    
  }

  public void move(int dx, int dy) {
    
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
  
  public boolean isUnit() {
    return true;
  }
  
  public String getName() {
    return name;
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
    if (nextTile == null) {
      System.out.println("Fuck. " + (getX()+dx) + (getY()+dy));
    }
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
            nextTargetUnit = targetUnit;
            setCurrentActivity("standing");
            setNextActivity("attacking");
            System.out.println("moved onto target unit.");
          } else if (blockingUnit.isMoving()) {
            /* Nothing queued up, just moving to a spot. */
            if (nextActivity == null) {
              setCurrentActivity("standing");
              setNextActivity("walking");
              setNextTargetPosn(targetPosn);
              setTargetPosn(null);
            /* Attacking is queued up; keep it queued. */
            } else if (nextActivity.equals("attacking")) { 
              setCurrentActivity("standing");
            }
            System.out.println("waiting.");
          } else { 
            setCurrentActivity("standing");
            clearTargets();
            System.out.println("blocked else case");
          }
        // If the target posn is blocked by an object - maybe this would
        // happen with fog of war - stop pathing.
        } else {
          setCurrentActivity("standing");
          clearTargets();
          System.out.println("other block?");
        }
      /* If we're not next to the target posn and our next tile is
       * blocked, compute a new path.  This fails if the target tile is
       * perma-blocked - what do we do? */
      
      } else { // blocked and nextposn != targetposn
        blockingUnit = nextTile.getUnit();
        if (blockingUnit != null && blockingUnit.isMoving()) {
          /* Just walking, no unit target */
          if (nextActivity == null) {
            setCurrentActivity("standing");
            nextTargetPosn = targetPosn;
            targetPosn = null;
            setNextActivity("walking");
          } else if (nextActivity.equals("attacking")) {
            setCurrentActivity("standing");
          }
        } else {
          /* If the path is blocked by an object or a non-moving unit, better re-path around it. */
          
          /* Just walking, no unit target */
          if (nextActivity == null) {
            setCurrentActivity("standing");
            nextTargetPosn = targetPosn;
            targetPosn = null;
            setNextActivity("walking");
            setPath(game.findPath(getPosn(), nextTargetPosn));
          } else if (nextActivity.equals("attacking")) {
            setCurrentActivity("standing");
            targetPosn = null;
            setPath(game.findPath(this, nextTargetUnit));
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
    //game.getFloorOverlays().remove(floorOverlay);
    if (floorOverlay != null) {
      game.getDepthTree().remove(floorOverlay);
      floorOverlay = null;
    }
    if (game.getHumanPlayer().isHostile(getPlayer())) {
      boolean unitIsTargeted = false;
      for (Unit u: game.getHumanPlayer().getSelectedUnits()) {
        if (u.getTargetUnit() != null && u.getTargetUnit().equals(this)) {
          unitIsTargeted = true;
        }
      }
      if (unitIsTargeted) {
        Color transRed = new Color(255,0,0,64);
        floorOverlay = new FloorOverlay(game, this, Color.RED, transRed);
      } else {
        floorOverlay = new FloorOverlay(game, this, Color.RED);
      }
    } else {
      if (game.getHumanPlayer().getSelectedUnits().contains(this)) {
        Color transGreen = new Color(0,255,0,64);
        floorOverlay = new FloorOverlay(game, this, Color.GREEN, transGreen);
      } else {
        floorOverlay = new FloorOverlay(game, this, Color.GREEN);
      }
    }
    //game.addFloorOverlay(floorOverlay);
    game.getDepthTree().add(floorOverlay);
  }
  
  public boolean isMoving() {
    return currentActivity.equals("walking");
  }
  
  // ===== ACCESSOR METHODS =====
  
  public void setTargetUnit(Unit u) {
    targetUnit = u;
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
    while (i < animations.length) {
      if (!animations[i].getActivity().equals(activity)) {
        i += 8;
      } else if (!animations[i].getDirection().equals(direction)) {
        i++;
      } else {
        // It's a match!
        currentAnimation = animations[i];
        currentAnimation.setIndex(0);
        break;
      }
    }
    if (currentAnimation == null) {
      //System.out.println("fuck, couldn't find animation");
      currentAnimation = oldAnimation;
    }
    for (Accessory e : equipment.values()) {
      e.setCurrentAnimation(activity, direction);
    }
  }

  public String getAnimationName() {
    return animationName;
  }
  
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
    // TODO Auto-generated method stub
    //System.out.println(this + " hit unit " + u);
  }
  
  public abstract void playHitSound();
  public abstract void playBashSound();

  public void doBashHit(Unit u) {
    int d = 1; // ...
    u.takeHit(this, d);
  }
  public void setNextTargetUnit(Unit u) {
    nextTargetUnit = u;
  }
  
  public void setNextTargetPosn(Posn p) {
    nextTargetPosn = p;
  }
  
  public void takeDamage(int dmg) {
    if (dmg >= currentHP) {
      currentHP = 0;
      // where do we check for death?
    } else {
      currentHP -= dmg;
    }
    // sound FX, blood, etc will go here too
  }
  
  public void takeHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX()+dx, getY()+dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      
    } else {
      takeDamage(dmg);
    }
  }

  public boolean isBlocking() {
    // TODO Auto-generated method stub
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
    top -= 20;
    
    healthBar.draw(g, left, top);
  }
  
  public void addAccessory(Accessory e) {
    equipment.put(e.getSlot(), e);
    e.setCurrentAnimation(this.getCurrentActivity(), game.coordsToDir(dx,dy));
  }

  public void setNextActivity(String activity) {
    // TODO Auto-generated method stub
    nextActivity = activity;
  }

}
