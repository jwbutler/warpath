package warpath.units;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import jwbgl.*;

import warpath.core.Activity;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.core.Constants;
import warpath.core.Logging;
import warpath.internals.Direction;
import warpath.core.RPG;
import warpath.core.Utils;
import warpath.items.Accessory;
import warpath.items.ItemSlot;
import warpath.objects.BasicObject;
import warpath.objects.GameObject;
import warpath.objects.Tile;
import warpath.players.Player;
import warpath.objects.FloorOverlay;
import warpath.ui.components.HealthBar;
import warpath.ui.components.TransHealthBar;

/**
 * Class representing any type of unit.  This class should be extended by any
 * subsequent unit classes we define.  Not to be used by itself (abstract).
 */
public abstract class BasicUnit extends BasicObject implements GameObject, Unit {
  private static final long serialVersionUID = 1L;
  protected final static int WALK_MOVE_FRAME = 2;
  protected final static int ATTACK_HIT_FRAME = 2;
  
  protected final static int BLOCK_COST = 2; // costs N EP per tick (does not disable HP regen)
  protected final static int ATTACK_COST = 15; // 10 frames per attack for 67% uptime
  protected final static int BASH_COST = 36; // 12 frames per bash for 33% uptime
  protected final static int SLASH_COST = 3; // costs N EP per tick (does not disable HP regen) // SHOULD BE 2
  
  // TODO convert these to per-second amounts, deal with the fallout
  protected final static int HP_REGEN = 10; // regen 1 HP per N ticks
  protected final static int EP_REGEN = 1; // regen 1 EP per N ticks
  
  protected final static int X_OFFSET = 0;
  protected final static int Y_OFFSET = -32;
  
  private static final Map<String, Surface> frameCache = new HashMap<>();
  private final List<Animation> animations;
  private final Map<ItemSlot, Accessory> equipment;
  private final Map<Color, Color> paletteSwaps;
  private final List<Activity> activities;
  private final String name;
  private final String spriteName;
  private int hpBarOffset;
  
  private int dx;
  private int dy;
  private int currentHP, maxHP;
  private int currentEP, maxEP;
  private boolean slashDirectionIsNew;

  private Unit targetUnit;
  private Unit nextTargetUnit;
  private Posn targetPosn;
  private Posn nextTargetPosn;
  private Activity currentActivity;
  private Activity nextActivity;
  
  private Animation currentAnimation;
  private Player player;
  private FloorOverlay floorOverlay;
  private FloorOverlay targetPosnOverlay;
  private HealthBar healthBar;
  private List<Posn> path;

  public BasicUnit(String name, String spriteName, List<Activity> activities, Map<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(posn);
    this.name = name;
    this.spriteName = spriteName;
    this.activities = activities;
    this.player = player;
    equipment = new HashMap<>();
    this.paletteSwaps = paletteSwaps;
    this.path = new LinkedList<>();

    setDirection(Direction.N);
    // Sprites are 80x80 (scaled), tiles are 96x48 (scaled).  There's an 8
    // pixel space at bottom of player sprites. */
    setOffsets(0, -32);
    animations = new ArrayList<>();
    loadAnimations();
    applyPaletteSwaps();
    setCurrentActivity(Activity.STANDING);
    //player.getUnits().add(this); // NO, the game will do this.
    updateFloorOverlay();
    healthBar = new TransHealthBar(this, 48, 12);
    hpBarOffset = -20;
    setSlashDirectionIsNew(false);
    //System.out.printf("%s has %d frames\n",getClass(), frames.keySet().size());
  }

  /**
   * Apply each of the palette swaps to each animation.
   */
  public void applyPaletteSwaps() {
    for (Animation anim: animations) {
      for (Surface s: anim.getFrames()) {
        s.setPaletteSwaps(paletteSwaps);
        s.applyPaletteSwaps();
      }
    }
  }

  /**
   * Face the unit toward the specified point.
   * That is, make (dx,dy) into an integer unit vector.
   * We're using Pythagorean distance rather than Civ distance here,
   * should we change?
   * @param x - the x coordinate of the tile to point at
   * @param y - the y coordinate of the tile to point at
   */
  public void pointAt(int x, int y) {
    int dx = x - this.getX();
    int dy = y - this.getY();
    double distance = Math.sqrt(dx*dx + dy*dy);
    if (distance != 0) {
      this.dx = (int) Math.round(dx/distance);
      this.dy = (int) Math.round(dy/distance);
    }
  }
  
  /**
   * @see #pointAt(int, int)
   */
  public void pointAt(Posn posn) {
    pointAt(posn.getX(), posn.getY());
  }
  
  /**
   * @see #pointAt(int, int)
   */
  public void pointAt(GameObject target) {
    pointAt(target.getX(), target.getY());
  }

  /**
   * Loads all of the animations for this unit.
   * Calls {@link #loadActivityAnimations} for each activity. (or does it...)
   */
  public void loadAnimations() {
    long t = System.currentTimeMillis();
    for (Activity activity : activities) {
      loadActivityAnimations(activity);
    }
    
    for (Accessory e: equipment.values()) {
      e.loadAnimations();
    }
    t = System.currentTimeMillis() - t;
    System.out.printf("%s.loadAnimations(): %d ms\n", this.getClass(), t);
  }
  
  /**
   * Extend this as needed for animations that have different versions for
   * different units. Eventually that will likely be all of them.
   */
  public void loadActivityAnimations(Activity activity) {
    if (activity.equals(Activity.FALLING)) {
      loadFallingAnimations();
    } else {
      loadGenericAnimations(activity);
    }
  }
  
  /**
   * Used to load any animation that follows the general pattern without
   * exceptions.
   */
  protected void loadGenericAnimations(Activity activity, List<String> filenames) {
    for (Direction dir : Direction.directions()) {
      animations.add(Animation.fromTemplate(spriteName, activity, dir, filenames, frameCache));
    }
  }
  
  /**
   * Used to load any animation that follows the general pattern without
   * exceptions, where the filenames are specified in AnimationTemplates.
   */
  protected void loadGenericAnimations(Activity activity) {
    loadGenericAnimations(activity, AnimationTemplates.getTemplate(activity));
  }
  
  /**
   * Falling has a few variations; this is for human units I think.
   * Two directions, NE or S.
   */
  protected void loadFallingAnimations() {
    for (Direction dir : Direction.directions()) {
      List<Direction> neDirections = Arrays.asList(Direction.N, Direction.NE, Direction.E, Direction.SE);
      if (neDirections.contains(dir)) {
        animations.add(Animation.fromTemplate(spriteName, Activity.FALLING, Direction.NE, AnimationTemplates.FALLING, frameCache));
      } else {
        animations.add(Animation.fromTemplate(spriteName, Activity.FALLING, Direction.S, AnimationTemplates.FALLING, frameCache));
      }
    }
  }

  /**
   * Increments the frame of the unit's current animation, as well as for each
   * of its accessories.
   */
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
  
  /**
   * This is called whenever the unit reaches the end of its current animation
   * to determine the next activity.  There is a LOT of event processing going
   * on here.  Also, this method is used to handle the full activity tree of
   * all the units in the game.
   * TODO Find a way to subdivide this method so that we can override parts of
   * it in subclasses.
   */
  public void nextActivity() {
    RPG game = RPG.getInstance();
    if (currentActivity.equals(Activity.FALLING)) {
      // If we've reached the end of our falling animation, get ready to die.
      game.queueRemoveUnit(this);
      die();
    } else if (currentActivity.equals(Activity.BLOCKING_1)) {
      // If we've started blocking, either continue the block or terminate it
      // based on whether we've queued up additional blocking.
      if (nextActivity != null && nextActivity.equals(Activity.BLOCKING_2)) {
        pointAt(nextTargetPosn);
        setCurrentActivity(Activity.BLOCKING_2);
      } else {
        setCurrentActivity(Activity.BLOCKING_3);
      }
    } else if (currentActivity.equals(Activity.BLOCKING_2)) {
      // If we're currently blocking, either continue the block or terminate it
      // based on whether we've queued up additional blocking; also, check
      // whether we have enough EPs to continue blocking.
      if (nextActivity != null && nextActivity.equals(Activity.BLOCKING_2) && currentEP >= getBlockCost()) {
        pointAt(nextTargetPosn);
        setCurrentActivity(Activity.BLOCKING_2);
      } else {
        setCurrentActivity(Activity.BLOCKING_3);
      }
    
    // BEGIN SHAMELESS C/P
    } else if (currentActivity.equals(Activity.SLASHING_1)) {
      if (nextActivity != null && nextActivity.equals(Activity.SLASHING_2)) {
        pointAt(nextTargetPosn);
        setCurrentActivity(Activity.SLASHING_2);
      } else {
        setCurrentActivity(Activity.SLASHING_3);
      }
    } else if (currentActivity.equals(Activity.SLASHING_2)) {
      if (nextActivity == null) {
        setCurrentActivity(Activity.SLASHING_3);
      } else if (nextActivity.equals(Activity.SLASHING_2) && currentEP >= getSlashCost()) {
        pointAt(nextTargetPosn);
        setCurrentActivity(Activity.SLASHING_2);
      } else {
        setCurrentActivity(Activity.SLASHING_3);
      }
    // END SHAMELESS C/P
    } else if (currentActivity.equals(Activity.TELEPORTING)) {
      moveTo(getTargetPosn());
      setCurrentActivity(Activity.TELEPORTING);
      setTargetPosn(null);
      currentEP -= RobedWizardUnit.TELEPORT_COST;
      
    // If next activity is rezzing: (wizard)
    // 1) Make sure we're standing on the corpse.
    // 2) Make sure there's a free adjacent square to put the zombie on.
    // ...
    //
    } else if (currentActivity.equals(Activity.REZZING)) {
      setCurrentActivity(Activity.STANDING);
      setTargetPosn(null);
      GameObject targetCorpse = null;
      for (GameObject o : game.getFloor().getTile(getPosn()).getObjects()) {
        if (o.isCorpse()) {
          boolean allTilesBlocked = true;
          targetCorpse = o;
          for (int i = 0; i < AnimationTemplates.WIZARD_REZZING.size(); i++) {
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

            game.queueAddUnit(new EnemyZombie(String.format("Zombie %d", game.nextEnemyID()), p, game.getPlayer(2)));
          } else {
            // What should we do if all the tiles are blocked?
            // TODO Make sure there is not an infinite loop here.
            System.out.println("help");
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
      if (lastTargetUnit != null) {
        lastTargetUnit.updateFloorOverlay();
      }
      if (nextActivity.equals(Activity.WALKING)) {
        if (getPosn().equals(targetPosn)) {
          setCurrentActivity(Activity.STANDING);
          targetPosn = null;
          nextActivity = null;
          setNextTargetPosn(null);
        } else {
          setPath(game.findPath(getPosn(), targetPosn));
          
          // The pathing algorithm is configured to ignore units that are
          // currently moving.  This is for the case where the first tile in
          // the path contains a moving unit.
          if (game.getFloor().getTile(path.get(0)).isBlocked()) {
            Logging.debug("fux");
          } else {
            pointAt(path.get(0));
            setCurrentActivity(Activity.WALKING);
            nextActivity = null;
            setNextTargetPosn(null);
            setNextTargetUnit(null);
          }
        }
      // If next activity is attacking, we might have to path to the unit first.
      } else if (nextActivity.equals(Activity.ATTACKING)) {
        if (Utils.distance(this, targetUnit) == 1) {
          if (currentEP >= ATTACK_COST) {
            pointAt(targetUnit);
            setCurrentActivity(Activity.ATTACKING);
            nextActivity = null;
            setNextTargetPosn(null);
            setNextTargetUnit(null);
            currentEP -= ATTACK_COST;
          } else {
            setCurrentActivity(Activity.STANDING);
            targetPosn = null;
            nextActivity = null;
            setNextTargetPosn(null);
          }
        } else {
          setTargetPosn(targetUnit.getPosn());
          setPath(game.findPath(getPosn(), targetPosn));
          pointAt(path.get(0));
          setCurrentActivity(Activity.WALKING);
          // Don't clear nextActivity/nextTargetUnit 
        }
      } else if (nextActivity.equals(Activity.BASHING)) {
        if (Utils.distance(this, targetUnit) == 1) {
          if (currentEP >= BASH_COST) {
            pointAt(targetUnit);
            setCurrentActivity(Activity.BASHING);
            nextActivity = null;
            setNextTargetPosn(null);
            setNextTargetUnit(null);
            currentEP -= BASH_COST;
          } else {
            setCurrentActivity(Activity.STANDING);
            targetPosn = null;
            nextActivity = null;
            setNextTargetPosn(null);
          }
        } else {
          setTargetPosn(targetUnit.getPosn());
          setPath(game.findPath(getPosn(), targetPosn));

          if (game.getFloor().getTile(path.get(0)).isBlocked()) {
            // System.out.println("fux2");
          } else {
            pointAt(path.get(0));
            setCurrentActivity(Activity.WALKING);
          }
          // Don't clear nextActivity/nextTargetUnit 
        }
      } else if (nextActivity.equals(Activity.BLOCKING_1)) {
        setTargetPosn(getNextTargetPosn());
        pointAt(targetPosn);
        setCurrentActivity(Activity.BLOCKING_1);
        //System.out.printf("startblock - %s - %s\n", getPosn(), targetPosn);
        setNextActivity(Activity.BLOCKING_2);
      } else if (nextActivity.equals(Activity.SLASHING_1)) {
        setTargetPosn(getNextTargetPosn());
        pointAt(targetPosn);
        setCurrentActivity(Activity.SLASHING_1);
        setNextActivity(Activity.SLASHING_2);
      } else if (nextActivity.equals(Activity.TELEPORTING)) {
        setTargetPosn(getNextTargetPosn());
        setCurrentActivity(Activity.TELEPORTING);
        setNextActivity(null);
        setNextTargetPosn(null);
      } else if (nextActivity.equals(Activity.REZZING)) {
        setCurrentActivity(Activity.REZZING);
        setNextActivity(null);
      }
      // Shouldn't need to do this anymore now that setters do it for us automatically. 
      //if (targetUnit != null) {
      //  targetUnit.updateFloorOverlay();
      //}
    // no nextactivity
    } else { // if (nextActivity == null) {
      if (currentActivity.equals(Activity.WALKING)) {
        refreshWalk();
      } else {
        setCurrentActivity(Activity.STANDING);
        setTargetUnit(null);
      }
    }
  }

  private void refreshWalk() {
    RPG game = RPG.getInstance();
    // This is the case where the next tile is non-empty but we're only
    // pausing for one turn. 
    if (targetPosn == null) {
      // System.out.println("Fuck!!!");
      //clearTargets();
      
    // These two cases basically represent the same thing: we've arrived, or
    // we're otherwise out of path for some reason. 
    } else if (path == null || path.size() == 0) {
      setCurrentActivity(Activity.STANDING);
      clearTargets();
    } else if (getPosn().equals(targetPosn)) {
      setCurrentActivity(Activity.STANDING);
      clearTargets();
    // Proceed.
    } else {
      // We're NOT calling checkNextTile here. It's OK, we'll check it before
      // doing anything else in doEvents() or something.
      if (game.getFloor().getTile(path.get(0)).isBlocked()) {
        //System.out.println("fux4");
      } else {
        pointAt(path.get(0));
        setCurrentActivity(Activity.WALKING);
      }
    }
  }

  public boolean isHostile(Unit u) {
    return getPlayer().isHostile(u.getPlayer());
  }

  /**
   * Performs various maintenance tasks at the beginning of each "turn".
   * TODO Think hard about the order of execution.
   */
  public void doUpkeep() {
    RPG game = RPG.getInstance();
    if (getCurrentActivity().equals(Activity.BLOCKING_2)) {
      currentEP -= getBlockCost();
    } else if (getCurrentActivity().equals(Activity.SLASHING_2)) {
      currentEP -= getSlashCost();
    }

    // TODO awk
    if (!getCurrentActivity().equals(Activity.FALLING)) {
      if ((game.getTicks() % HP_REGEN == 0) && (currentHP < maxHP)) {
        currentHP++;
      }
      if ((game.getTicks() % EP_REGEN == 0) && (currentEP < maxEP)) {
        currentEP++;
      }
    }
    if (currentEP < 0) {
      currentEP = 0;
    }
    this.nextFrame();
  }
  
  protected void clearTargets() {
    setTargetPosn(null);
    setTargetUnit(null);
    setNextTargetPosn(null);
    setNextTargetUnit(null);
    nextActivity = null;
    setTargetPosnOverlay(null);
  }

  /**
   * So this is an important method.
   * It's a big mess.
   * The idea here is to handle ALL the activities for ALL unit types here,
   * so we won't have to copy and paste it into all the unit subclasses.
   * (They will each have their own nextActivity() logic.)
   * TODO figure out a way to split this method up.
   */
  public void doEvents() {
    RPG game = RPG.getInstance();
    if (getCurrentActivity().equals(Activity.WALKING)) {
      // If the unit is walking and its target tile is blocked,
      // handle it (cancel, etc. - checkNextTile does a lot.)
      doWalkEvents();
    }
    
    // The attack hit occurs on frame 2; maybe we can generalize this.
    else if (getCurrentActivity().equals(Activity.ATTACKING)) {
      if (getCurrentAnimation().getIndex() == getAttackHitFrame()) {
        // What happens if the unit has moved away?
        Posn nextPosn = new Posn(getX()+dx, getY()+dy);
        
        if (targetUnit.getPosn().equals(nextPosn)) {
          doAttackHit(targetUnit);
        }
      }
    } else if (getCurrentActivity().equals(Activity.BASHING)) {
      if (getCurrentAnimation().getIndex() == 0) {
        if (Utils.distance(this, targetUnit) > 1) {
          setPath(game.findPath(this, targetUnit));
          if (path != null && path.size() > 0) {
            targetPosn = targetUnit.getPosn();
            if (game.getFloor().getTile(path.get(0)).isBlocked()) {
              // System.out.println("fux5");
            } else {
              setCurrentActivity(Activity.WALKING);
            }
          } else {
            setCurrentActivity(Activity.STANDING);
            clearTargets();
            Logging.debug("uh oh");
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
    } else if (getCurrentActivity().equals(Activity.BLOCKING_2)) {
      // WTF?
      if (currentHP == 0) {
        setNextActivity(null);
        setNextTargetPosn(null);
      }
    } else if (getCurrentActivity().equals(Activity.SLASHING_2)) {
      if (getCurrentAnimation().getIndex() == 0) {
        if (getSlashDirectionIsNew()) {
          setSlashDirectionIsNew(false);
          Posn nextPosn = getPosn().add(getDirection().toPosn());
          Unit tu = game.getFloor().getTile(nextPosn).getUnit();
          if (tu != null && isHostile(tu)) {
            doSlashHit(tu);
          }
        }
      }
    }
  }

  /**
   * Moved checkNextTile() into this function, because it had grown to
   * encompass nearly all walking-related events so I figured I would
   * codify that.
   */
  public void doWalkEvents() {
    RPG game = RPG.getInstance();
    if (getCurrentAnimation().getIndex() <= getWalkMoveFrame()) {
      Tile nextTile = game.getFloor().getTile(getPosn().add(getDirection().toPosn())); // should match path.first
      if (nextTile.isBlocked()) {
        // If our target posn is next in the path and it's blocked, cancel the movement.
        // Is this good?
        if (targetPosn != null && nextTile.getPosn().equals(targetPosn)) {
          // note that we're using setCurrentActivity, not setNextActivity

          // If there's a unit at the target position:
          // if it's walking, wait for it to move,
          // otherwise, cancel pathing.
          Unit blockingUnit = nextTile.getUnit();
          if (blockingUnit != null) {
            if (targetUnit != null && blockingUnit.equals(targetUnit)) {
              setNextTargetUnit(targetUnit);
              setNextActivity(Activity.ATTACKING);
              setCurrentActivity(Activity.STANDING);
              setTargetUnit(null);
            } else if (blockingUnit.isMoving()) {
              // Nothing queued up, just moving to a spot.
              if (nextActivity == null || nextActivity.equals(Activity.WALKING)) {
                setCurrentActivity(Activity.STANDING);
                //System.out.println("W1");
                setNextActivity(Activity.WALKING);
                setNextTargetPosn(targetPosn);
                setTargetPosn(null);
                // Attacking is queued up; keep it queued.
              } else if (nextActivity.equals(Activity.ATTACKING)) { // || nextActivity.equals("blocking")) {
                setCurrentActivity(Activity.STANDING);
              } else {
                Logging.debug("Next tile is blocked, unexpected nextActivity...");
                printDebug();
              }
            } else {
              // There's a unit on our target posn and it's not moving: cancel pathing.
              setCurrentActivity(Activity.STANDING);
              clearTargets();
            }
            // If the target posn is blocked by an object - maybe this would
            // happen with fog of war - stop pathing.
          } else {
            setCurrentActivity(Activity.STANDING);
            clearTargets();
          }

          // If we're not next to the target posn and our next tile is
          // blocked, compute a new path.  This fails if the target tile is
          // perma-blocked - what do we do?

        } else { // blocked and nextposn != targetposn
          Unit blockingUnit = nextTile.getUnit();
          if (blockingUnit != null && blockingUnit.isMoving()) {
            // Just walking, no unit target
            if (nextActivity == null || nextActivity.equals(Activity.WALKING)) {
              setCurrentActivity(Activity.STANDING);
              setNextTargetPosn(targetPosn);
              setTargetPosn(null);
              setNextActivity(Activity.WALKING);
            } else if (nextActivity.equals(Activity.ATTACKING) || nextActivity.equals(Activity.BASHING)) {
              setCurrentActivity(Activity.STANDING);
              setTargetPosn(null);
            } else {
              //printDebug();
            }
          } else {
            // If the path is blocked by an object or a non-moving unit, better re-path around it.

            // Just walking, no unit target
            if (nextActivity == null) {
              setCurrentActivity(Activity.STANDING);
              setNextTargetPosn(targetPosn);
              targetPosn = null;
              setNextActivity(Activity.WALKING);
              setPath(game.findPath(getPosn(), nextTargetPosn));
            } else if (nextActivity.equals(Activity.ATTACKING) || nextActivity.equals(Activity.BASHING)) {
              setCurrentActivity(Activity.STANDING);
              targetPosn = null;
              setPath(game.findPath(this, nextTargetUnit));
            } else {
              Logging.debug("Debug 3");
            }
          }
        }
      }
    }
    // If the unit is walking, the actual movement occurs on frame 2.
    if (getCurrentActivity().equals(Activity.WALKING)) {
      if (getCurrentAnimation().getIndex() == getWalkMoveFrame()) {
        moveBy(dx, dy);
        path.remove(0);
      }
    }
  }

  protected int getAttackHitFrame() {
    return ATTACK_HIT_FRAME;
  }

  protected int getWalkMoveFrame() {
    return WALK_MOVE_FRAME;
  }

  // deal with falling animations, sfx, etc later.
  // What to do with overlays?
  public void die() {
    RPG.getInstance().removeObject(getFloorOverlay());
    floorOverlay = null;
  }
  
  /**
   * Moves the unit by the specified amount.
   * Does NOT validate the tile we're moving to. You have to do that yourself!
   * checkNextTile() was supposed to do that but it kind of grew in scope.
   */
  public void moveBy(int dx, int dy) {
    RPG game = RPG.getInstance();
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

  public void moveBy(Posn p) {
    moveBy(p.x, p.y);
  }
  
  /**
   * Moves the unit to an absolute location, in Posn form.
   * Like {@link #moveBy(int, int)}, this method does not validate.
   * @param p - the Posn to moveBy to
   */
  public void moveTo(Posn p) {
    RPG game = RPG.getInstance();
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


  
  /**
   * We can call this on any GameObject for differentiation purposes.
   */
  @Override
  public boolean isUnit() {
    return true;
  }
  
  public String getName() {
    return name;
  }
  public Activity getNextActivity() {
    return nextActivity;
  }
  
  @Override
  public String toString() {
    return String.format(
      "<%s - %s (%s) - %s_%s_%d",
      getName(),
      getCurrentActivity(),
      getNextActivity(),
      getCurrentAnimation().getActivity(),
      getCurrentAnimation().getDirection(),
      getCurrentAnimation().getIndex()
    );
  }

  // Add the appropriate red/green highlight under the unit.
  // Eventually we'll want some other colors for neutral units, NPCs, etc.
  public void updateFloorOverlay() {
    RPG game = RPG.getInstance();
    if (floorOverlay != null) {
      game.getDepthTree().remove(floorOverlay);
      setFloorOverlay(null);
    }
    if (game.getHumanPlayer().isHostile(getPlayer())) {
      if (game.getPlayerUnit().getTargetUnit() == this) {
        setFloorOverlay(new FloorOverlay(this, Color.RED, FloorOverlay.TRANS_RED));
      } else {
        setFloorOverlay(new FloorOverlay(this, Color.RED));
      }
    } else {
      setFloorOverlay(new FloorOverlay(this, Color.GREEN, FloorOverlay.TRANS_GREEN));
      //floorOverlay = new FloorOverlay(game, this, Color.GREEN);
    }
    game.getDepthTree().add(floorOverlay);
  }
  
  public boolean isMoving() {
    return currentActivity.equals(Activity.WALKING);
  }
  
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
  
  private void setPath(List<Posn> path) {
    this.path = path;
  }

  // Important: this takes a Posn as an argument, not a FloorOverlay
  // TODO this should be private!
  public void setTargetPosnOverlay(Posn posn) {
    RPG game = RPG.getInstance();
    if (targetPosnOverlay != null) {
      game.getDepthTree().remove(targetPosnOverlay);
      targetPosnOverlay = null;
    }
    if (posn != null) {
      targetPosnOverlay = new FloorOverlay(game.getFloor().getTile(posn), Color.CYAN);
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
  
  public Activity getCurrentActivity() {
    return currentActivity;
  }
  
  public void setCurrentActivity(Activity newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
  
  protected void setCurrentAnimation(Activity activity, Direction direction) {
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

  public String getSpriteName() {
    return spriteName;
  }

  public Direction getCurrentDirection() {
    return Direction.from(dx, dy);
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
    Logging.info("OMG SLASH HIT");
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
      if (!getCurrentActivity().equals(Activity.FALLING)) {
        setCurrentActivity(Activity.FALLING);
      }
    } else {
      currentHP -= dmg;
    }
  }
  
  /**
   * Source is a GameObject, not a unit - allows for stuff like projectiles,
   * floor fire, etc.
   */
  public void takeHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX() + dx, getY() + dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      // Do we want to take partial damage? Do we want to block adjacent angles?
    } else {
      takeDamage(dmg);
    }
  }
  
  public void takeBashHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX()+dx, getY()+dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      // Do we want to take partial damage? Do we want to block adjacent angles?
    } else {
      setCurrentActivity(Activity.STUNNED_SHORT);
      clearTargets();
      takeDamage(dmg);
    }
  }
  
  public void takeSlashHit(GameObject src, int dmg) {
    Posn blockedPosn = new Posn(getX() + dx, getY() + dy);
    if (isBlocking() && src.getPosn().equals(blockedPosn)) {
      // Do we want to take partial damage? Do we want to block adjacent angles?
    } else {
      setCurrentActivity(Activity.STANDING);
      clearTargets();
      takeDamage(dmg);
    }
  }

  /**
   * For the time being, we're treating the different blocking animations as
   * equivalent for mitigation purposes. Think about whether this is what
   * we want.
   */
  public boolean isBlocking() {
    if (getCurrentActivity().equals(Activity.BLOCKING_1)) return true;
    else if (getCurrentActivity().equals(Activity.BLOCKING_2)) return true;
    else if (getCurrentActivity().equals(Activity.BLOCKING_3)) return true;
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

  public Direction getDirection() {
    return Direction.from(dx, dy);
  }
  
  public void setDirection(Direction dir) {
    dx = dir.dx;
    dy = dir.dy;
  }
  
  public FloorOverlay getFloorOverlay() {
    return floorOverlay;
  }

  /**
   * Draw the unit plus all of its equipment.
   * TODO Apparently this method only handles sword/shield?
   * @param g - the AWT graphics object used to render it
   */
  public void draw(Graphics g) {
    RPG game = RPG.getInstance();

    if (equipment.get(ItemSlot.MAINHAND) != null) {
      if (equipment.get(ItemSlot.MAINHAND).drawBehind() ) {
        equipment.get(ItemSlot.MAINHAND).draw(g);
      }
    }
    if (equipment.get(ItemSlot.OFFHAND) != null) {
      if (equipment.get(ItemSlot.OFFHAND).drawBehind() ) {
        equipment.get(ItemSlot.OFFHAND).draw(g);
      }
    }
    super.draw(g);
    if (equipment.get(ItemSlot.MAINHAND) != null) {
      if (!equipment.get(ItemSlot.MAINHAND).drawBehind()) {
        equipment.get(ItemSlot.MAINHAND).draw(g);
      }
    }
    if (equipment.get(ItemSlot.OFFHAND) != null) {
      if (!equipment.get(ItemSlot.OFFHAND).drawBehind() ) {
        equipment.get(ItemSlot.OFFHAND).draw(g);
      }
    }
    Posn pixel = game.gridToPixel(getPosn()); // returns top left
    int left = pixel.getX() + Constants.TILE_WIDTH/2 - healthBar.getWidth()/2 + getXOffset();
    int top = pixel.getY() + Constants.TILE_HEIGHT/2 - healthBar.getHeight()/2 + getYOffset();
    top += hpBarOffset;
    
    healthBar.draw(g, left, top);
  }
  
  public void addAccessory(Accessory e) {
    equipment.put(e.getSlot(), e);
    e.setCurrentAnimation(this.getCurrentActivity(), getDirection());
  }

  @Override
  public void setNextActivity(Activity activity) {
    nextActivity = activity;
  }

  public Unit getNextTargetUnit() {
    return nextTargetUnit;
  }

  public Posn getNextTargetPosn() {
    return nextTargetPosn;
  }
  
  public void printDebug() {
    Logging.debug(
      String.format(
        "<%s - %s %s %s %s %s %s>\n",
        getClass(),
        currentActivity,
        nextActivity,
        targetPosn,
        nextTargetPosn,
        targetUnit,
        nextTargetUnit
      )
    );
  }

  public void setFloorOverlay(FloorOverlay floorOverlay) {
    this.floorOverlay = floorOverlay;
  }

  // TODO this should be private
  public FloorOverlay getTargetPosnOverlay() {
    return targetPosnOverlay;
  }

  public int getBlockCost() {
    return BLOCK_COST;
  }

  public int getSlashCost() {
    return SLASH_COST;
  }

  public boolean getSlashDirectionIsNew() {
    return slashDirectionIsNew;
  }

  public void setSlashDirectionIsNew(boolean slashDirectionIsNew) {
    this.slashDirectionIsNew = slashDirectionIsNew;
  }

  public Map<String, Surface> getFrameCache() {
    return frameCache;
  }

  public List<Animation> getAnimations() {
    return animations;
  }

  public Map<ItemSlot, Accessory> getEquipment() {
    return equipment;
  }

  public Map<Color, Color> getPaletteSwaps() {
    return paletteSwaps;
  }

  public List<Activity> getActivity() {
    return activities;
  }

  public HealthBar getHealthBar() {
    return healthBar;
  }

  public List<Posn> getPath() {
    return path;
  }

  public void setCurrentHP(int hp) {
    currentHP = hp;
  }

  public void setMaxHP(int hp) {
    maxHP = hp;
  }

  public void setCurrentEP(int ep) {
    currentEP = ep;
  }

  public void setMaxEP(int ep) {
    maxEP = ep;
  }

  // TODO this really needs to be refactored out
  public void setHPBarOffset(int offset) {
    hpBarOffset = offset;
  }
}
