package warpath.core;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.Timer;

import jwbgl.*;

import warpath.activities.Activity;
import warpath.internals.DepthTree;
import warpath.internals.Direction;
import warpath.internals.PathfinderEntry;
import warpath.internals.PathfinderPQ;
import warpath.levels.Level;
import warpath.levels.TestLevel;
import warpath.objects.Floor;
import warpath.objects.GameObject;
import warpath.objects.Tile;
import warpath.players.AIPlayer;
import warpath.players.HumanPlayer;
import warpath.players.Player;
import warpath.templates.TemplateFactory;
import warpath.templates.UnitTemplate;
import warpath.ui.GameWindow;
import warpath.ui.SoundPlayer;
import warpath.units.SwordGuy;
import warpath.units.Unit;

/**
 * The main game engine.  Expect this one to be a few thousand lines long.
 */

public class RPG implements ActionListener {

  private static RPG instance;

  private final Timer frameTimer;
  private int ticks;
  private int nextEnemyID;
  private Floor floor;

  // Using a HashMap for this is pretty strange.  But it lets us access
  // players by number, which I think is useful.
  private final Map<Integer, Player> players = new HashMap<>();
  private final List<Unit> units = new ArrayList<>();
  private final List<GameObject> objects = new ArrayList<>(); // Non-units
  private final List<Level> levels = new ArrayList<>();

  // These are kinda hacks to get around concurrent modification exceptions.
  // Maybe there is a better solution.
  private final List<Unit> unitsToAdd = new ArrayList<>();
  private final List<Unit> unitsToRemove = new ArrayList<>();

  // Unsure if we should keep this reference, he's always going to be player 1, right?
  private HumanPlayer humanPlayer;

  private final DepthTree depthTree;
  private static final Random RNG = new Random();
  private final SoundPlayer soundPlayer;
  private final InputHandler inputHandler;

  // Not using anything from this except victory/defeat conditions.
  private int levelIndex;
  private Posn cameraPosn;

  private boolean redrawFloor;

  /**
   * Constructor for the main RPG class.
   */
  public RPG() {
    floor = new Floor(15, 15);
    frameTimer = new Timer(1000/Constants.FPS, this);
    ticks = 0;
    nextEnemyID = 1;
    addHumanPlayer();
    soundPlayer = new SoundPlayer();
    inputHandler = new InputHandler();
    //playSound("crystal.wav");

    depthTree = new DepthTree();
    cameraPosn = null;
    redrawFloor = true;
  }

  /**
   * Start the timer.  We might also use this to restart/unpause
   * This seems like a REALLY weird place to put the palette swaps (as a parameter).
   * @param template
   */
  public void start(UnitTemplate template) {
    // eugh
    GameWindow.getInstance().getGamePanel().init();
    // Add some player units.
    //HumanUnit u = new HumanUnit(me, "u", new Posn(3,4), me.getHumanPlayer());

    // This is a dumb workaround.
    setFloor(new Floor(1, 1));
    getFloor().setTile(0, 0, new Tile(new Posn(0, 0), "tile_48x24_grass.png"));
    SwordGuy u = new SwordGuy("u", new Posn(0,0), getHumanPlayer(), template.getPaletteSwaps());

    //SwordGirl u = new SwordGirl(me, "u", new Posn(3,4), me.getHumanPlayer());
    addUnit(u);

    // Make a hostile AI player
    addPlayer(2, new AIPlayer());
    getHumanPlayer().setHostile(getPlayer(2));
    getPlayer(2).setHostile(getPlayer(1));

    loadLevels();
    openLevel();
    centerCamera();
    frameTimer.start();
  }

  /**
   * Start the game without going through the character creator.
   */
  public void start() {
    UnitTemplate t = (UnitTemplate)TemplateFactory.getTemplate("player");
    start(t);
  }

  /**
   * Called every time the frame timer fires.
   * Redraws everything, then increments the tick counter.
   * TODO: IS THIS THE RIGHT ORDER OF OPERATIONS?
   * @param e - the Action event
   */
  public void actionPerformed(ActionEvent e) {
    doUpkeep();
  }

  /**
   * Fires at the start of the turn.
   */
  public void doUpkeep() {
    // Do blocking event code.  Should this go to Upkeep?
    doContinuousActivityUpkeep();

    for (Unit u: units) {
      u.doUpkeep();
    }

    for (Unit u: units) {
      u.doEvents();
    }

    addRemoveQueuedUnits();

    // Repaint() includes a call to drawAll().
    GameWindow.getInstance().repaint();

    // WHERE DOES THIS BELONG IN THE ORDER?
    checkVictoryConditions();

    incrementTicks();
  }

  private void addRemoveQueuedUnits() {
    for (Unit u : unitsToRemove) {
      removeUnit(u);
    }
    unitsToRemove.clear();
    for (Unit u : unitsToAdd) {
      addUnit(u);
    }
    unitsToAdd.clear();
  }

  private void checkVictoryConditions() {
    if (levels.get(levelIndex).checkVictory()) {
      levelIndex++;
      openLevel();
    }
  }

  /**
   * Handle persistent actions (i.e. bash, block, slash):
   * every upkeep, we check if the SHIFT / CTRL keys are held down and queue
   * up activities accordingly.
   * This fires at the beginning of the upkeep.
   * TODO the whole newSlashDirection thing seems inconsistent and awkward.
   * TODO moveBy to unit's upkeep + InputHandler logic?
   * Unlike most of the activity-related code, this should not be refactored
   * to the Activity classes because it's player unit-related.
   */
  public void doContinuousActivityUpkeep() {
    Unit playerUnit = getPlayerUnit();
    Posn mouseGridTile = pixelToGrid(InputHandler.getInstance().getMousePosn());

    // If CTRL is down, queue up a block.
    if (InputHandler.getInstance().ctrlIsDown()) {
      doBlockUpkeep(mouseGridTile);
    // If SHIFT is down, queue up a slash.
    } else if (InputHandler.getInstance().shiftIsDown()) {
      doSlashUpkeep(mouseGridTile);
    // Neither SHIFT nor CTRL is down; cancel blocks, bashes, slashes.
    } else {
      if (playerUnit.getNextActivity() != null) {
        // TODO bubble this up
        List<Activity> cancelActivity = Arrays.asList(
          Activity.BLOCKING_1, Activity.BLOCKING_2, Activity.SLASHING_1, Activity.SLASHING_2
        );

        if (cancelActivity.contains(playerUnit.getNextActivity())) {
          playerUnit.setNextActivity(null);
        }
      }
    }
  }

  /**
   * "Input handler" to continue a slash action.
   * Unlike most of the activity-related code, this should not be refactored
   * to the Activity classes because it's player unit-related.
   * @param mouseGridTile The grid tile that the mouse is hovering over
   */
  private void doSlashUpkeep(Posn mouseGridTile) {
    Unit playerUnit = getPlayerUnit();
    Activity currentActivity = playerUnit.getCurrentActivity();
    Activity nextActivity = playerUnit.getNextActivity();
    Direction dir = playerUnit.getDirection();
    Direction oldDir = playerUnit.getDirection();
    Posn lastTargetPosn = playerUnit.getPosn().add(dir.toPosn());
    Posn nextPosn = mouseGridTile;

    if (nextPosn == null) {
      nextPosn = lastTargetPosn;
    }
    // Calculate the tiles corresponding to rotating 45 degrees clockwise or
    // counterclockwise.
    Posn cwPosn = playerUnit.getPosn().add(dir.clockwise().toPosn());
    Posn ccwPosn = playerUnit.getPosn().add(dir.counterClockwise().toPosn());
    if (Utils.distance(cwPosn, nextPosn) < Utils.distance(ccwPosn, nextPosn)) {
      nextPosn = cwPosn;
    } else if (Utils.distance(ccwPosn, nextPosn) < Utils.distance(cwPosn, nextPosn)) {
      nextPosn = ccwPosn;
    } else {
      playerUnit.pointAt(nextPosn);
      if (playerUnit.getDirection().equals(oldDir)) {
        // do nothing
        // These two lines appear to be redundant.
        // getPlayerUnit().setDirection(oldDir);
        // getPlayerUnit().pointAt(getPlayerUnit().getPosn().add(oldDir));
        playerUnit.setSlashDirectionIsNew(false);
      } else {
        // If we pick a point that's in a straight line from the current
        // direction, it's not clear which way we should rotate; instead of
        // staying still, we will pick a direction randomly.
        if (RNG.nextBoolean()) {
          nextPosn = cwPosn;
        } else {
          nextPosn = ccwPosn;
        }
        playerUnit.setSlashDirectionIsNew(true);
      }
    }

    if (currentActivity.equals(Activity.STANDING) || currentActivity.equals(Activity.WALKING)) {
      if (nextActivity != null && nextActivity.equals(Activity.BASHING)) {
        // Bashing is queued up, let that happen.
      } else {
        if (mouseGridTile != null) {
          if (playerUnit.getCurrentEP() >= playerUnit.getSlashCost()) {
            // Queue up a slash order.
            playerUnit.setNextActivity(Activity.SLASHING_1);
            playerUnit.setNextTargetPosn(nextPosn);
            playerUnit.setTargetPosnOverlay(null);
            playerUnit.setSlashDirectionIsNew(true);
          }
        }
      }
    } else if (currentActivity.equals(Activity.SLASHING_1) || currentActivity.equals(Activity.SLASHING_2)) {
      if (mouseGridTile != null) {
        if (nextActivity != null && nextActivity.equals(Activity.BASHING)) {
          // Don't overwrite a queued bash order.
        } else {
          // Currently slashing, just set the next posn and continue slashing.
          // TODO do we need to set new slash direction?
          playerUnit.setNextTargetPosn(nextPosn);
          playerUnit.setTargetPosnOverlay(null);
        }
      }
    } else if (currentActivity.equals(Activity.BASHING) || currentActivity.equals(Activity.ATTACKING)) {
      if (mouseGridTile != null) {
        if (nextActivity != null && nextActivity.equals(Activity.BASHING)) {
          // Bashing is queued up, let that happen.
        } else {
          // Start a new slash.
          playerUnit.setNextActivity(Activity.SLASHING_1);
          playerUnit.setNextTargetPosn(nextPosn);
          playerUnit.setTargetPosnOverlay(null);
          playerUnit.pointAt(nextPosn);
          if (playerUnit.getDirection().equals(oldDir)) {
            playerUnit.setSlashDirectionIsNew(false);
          }
          playerUnit.setDirection(oldDir);
          playerUnit.pointAt(playerUnit.getPosn().add(oldDir.toPosn()));
        }
      }
    }
    // Determine whether we are slashing in a new direction.
    if (playerUnit.getNextActivity() != null && playerUnit.getNextActivity().equals(Activity.SLASHING_2)) {
      playerUnit.pointAt(playerUnit.getNextTargetPosn());
      playerUnit.setSlashDirectionIsNew(!oldDir.equals(playerUnit.getDirection()));
      playerUnit.pointAt(nextPosn);
      if (playerUnit.getDirection().equals(oldDir)) {
        playerUnit.setSlashDirectionIsNew(false);
      }
      playerUnit.setDirection(oldDir);
      playerUnit.pointAt(playerUnit.getPosn().add(oldDir.toPosn()));
    }
  }

  /**
   * Unlike most of the activity-related code, this should not be refactored
   * to the Activity classes because it's player unit-related.
   * @param mouseGridTile
   */
  private void doBlockUpkeep(Posn mouseGridTile) {
    Unit playerUnit = getPlayerUnit();
    Activity currentActivity = playerUnit.getCurrentActivity();
    Activity nextActivity = playerUnit.getNextActivity();
    if (currentActivity.equals(Activity.STANDING) || currentActivity.equals(Activity.WALKING)) {
      if (nextActivity != null && nextActivity.equals(Activity.BASHING)) {
        // Don't do anything, we're already getting ready to bash.
      } else {
        // Queue up a block order.
        if (mouseGridTile != null) {
          if (playerUnit.getCurrentEP() >= getPlayerUnit().getBlockCost()) {
            playerUnit.setNextActivity(Activity.BLOCKING_1);
            playerUnit.setNextTargetPosn(mouseGridTile);
            // TODO Is this the best way to handle target posn overlays?
            playerUnit.setTargetPosnOverlay(null);
          }
        }
      }
    } else if (currentActivity.equals(Activity.BLOCKING_1) || currentActivity.equals(Activity.BLOCKING_2)) {
      if (mouseGridTile != null) {
        if (nextActivity != null && nextActivity.equals(Activity.BASHING)) {
          // Don't do anything, we're already getting ready to bash.
        } else {
          // We're already blocking; continue blocking.
          playerUnit.setNextTargetPosn(mouseGridTile);
          playerUnit.setTargetPosnOverlay(null);
        }
      }
    } else if (currentActivity.equals(Activity.BASHING) || currentActivity.equals(Activity.ATTACKING)) {
      if (mouseGridTile != null) {
        if (nextActivity != null && nextActivity.equals(Activity.BASHING)) {
          // Don't do anything, we're already getting ready to bash.
        } else {
          // Queue up a block.
          playerUnit.setNextActivity(Activity.BLOCKING_1);
          playerUnit.setNextTargetPosn(mouseGridTile);
          playerUnit.setTargetPosnOverlay(null);
        }
      }
    }
  }

  /**
   * Make the human player and return it.  I'm not sure we need this.
   */
  public Player addHumanPlayer() {
    HumanPlayer player = new HumanPlayer(1);
    addPlayer(1, player);
    humanPlayer = player;
    return player;
  }

  //
  public void incrementTicks() {
    ticks++;
  }

  /**
   * Add a player with the specified player number.
   * TODO The whole playerNumber field is oddly handled.
   * @param playerNumber - the index to be used to retrieve this player
   * @param player - the Player object
   */
  public void addPlayer(int playerNumber, Player player) {
    players.put(playerNumber, player);
  }

  /**
   * Add the specified unit to all relevant lists (depth tree, floor units,
   * HUD panel).
   * Note that the unit is initialized with its controller established.
   * (I forget why :( )
   * @param u - The unit to add
   */
  public void addUnit(Unit u) {
    units.add(u);
    u.getPlayer().getUnits().add(u);
    if (u.getPlayer().equals(getHumanPlayer())) {
      GameWindow.getInstance().getHudPanel().init();
    }
    depthTree.add(u);
    floor.getTile(u.getX(),u.getY()).setUnit(u);
    u.updateFloorOverlay();
  }

  /**
   * Removes the specified unit from all relevant lists.
   * Specifically, each floor tile's list of units, and the RPG's units list,
   * as well as the RPG's depth tree.
   * Also removes its floor overlay from all relevant lists.
   * I don't think we need to check contains(), but it feels wrong...
   * @param u - The unit to remove
   */
  public void removeUnit(Unit u) {
    units.remove(u);
    depthTree.remove(u);

    // Remove the unit from its floor tile's list.
    floor.getTile(u.getX(),u.getY()).setUnit(null);

    // Remove the unit from its owner player's list.
    for (int i = 1; i < players.size()+1; i++) {
      Player p = players.get(i);
      if (p.getUnits().contains(u)) {
        p.getUnits().remove(u);
      }
    }

    // Find any units targeting this unit and clear their target.
    for (int i = 0; i < units.size(); i++) {
      if (units.get(i).getTargetUnit() != null && units.get(i).getTargetUnit().equals(u)) {
        units.get(i).setCurrentActivity(Activity.STANDING);
        units.get(i).setTargetUnit(null);
        units.get(i).setTargetPosn(null);
      }
    }

    // Remove this unit's floor overlay.
    removeObject(u.getFloorOverlay());
    u.setFloorOverlay(null);
  }

  /**
   * This returns the upper left corner of the grid tile.  Note that the
   * origin (0,0) is actually inside the menu bar; the playable area is
   * smaller than it "should" be
   * @param posn - the x and y coordinates of the grid tile
   * @return a Posn representing the pixel
   */
  public Posn gridToPixel(Posn posn) {

    int left, top;
    // Offsets are to make it so that pixel (0,0) is at the very top left of the floor
    int xOffset = floor.getHeight() * Constants.TILE_WIDTH / 2;
    int yOffset = 0;
    //System.out.println("offsets " + xOffset + " " + yOffset);
    int x = posn.getX();
    int y = posn.getY();
    left = (x * Constants.TILE_WIDTH / 2) - (y * Constants.TILE_WIDTH / 2) - getCameraX() + xOffset;
    top = (x * Constants.TILE_HEIGHT / 2) + (y * Constants.TILE_HEIGHT / 2) - getCameraY() - yOffset;
    return new Posn(left, top);
  }

  /**
   * @see #gridToPixel(Posn)
   * @param x - the x coordinate of the grid tile
   * @param y - the y coordinate of the grid tile
   */
  public Posn gridToPixel(int x, int y) {
    return gridToPixel(new Posn(x,y));
  }

  /**
   * Finds the nearest tile to a given pixel.  Returns null if it's outside
   * the map.
   * It works by checking EVERY floor tile, finding the coordinates at which
   * it's being drawn, and checking the transparency at the given point.
   * TODO If the coordinates are off the grid, find the closest valid grid
   * tile instead of returning null.
   * TODO isn't this massively inefficient?
   * @param pixel - The (x,y) coordinates of the pixel
   * @return The corresponding grid tile
   */
  public Posn pixelToGrid(Posn pixel) {
    // We need to restrict the range of X and Y, but... later
    for (int y = 0; y <= floor.getHeight(); y++) {
      for (int x = 0; x <= floor.getWidth(); x++) {
        Posn tilePixel = gridToPixel(x, y);
        int left = tilePixel.getX();
        int top = tilePixel.getY();
        Rect tileRect = new Rect(left, top, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
        if (tileRect.collidePoint(pixel.getX(), pixel.getY())) {
          int tileX = pixel.getX() - left;
          int tileY = pixel.getY() - top;
          try {
            int rgba = floor.getTile(x, y).getSurface().getRGB(tileX, tileY);
            Color c = new Color(rgba, true);
            if (c.getAlpha() == 255) {
              return new Posn(x,y);
            }
          } catch (NullPointerException e) {
            /* do nothing */
          }
        }
      }
    }
    return null;
  }

  /**
   * @see #pixelToGrid(Posn)
   * @param x - the x coordinate of the pixel
   * @param y - the y coordinate of the pixel
   */
  public Posn pixelToGrid(int x, int y) {
    return pixelToGrid(new Posn(x,y));
  }

  /**
   * Centers the camera on a given posn.
   * @param p
   */
  public void centerCamera(Posn p) {
    int x = p.getX();
    int y = p.getY();
    int xx = Constants.TILE_WIDTH/2 * (x + (floor.getHeight()-y)) - GameWindow.getInstance().getGamePanel().getWidth()/2;
    int yy = Constants.TILE_HEIGHT/2 * (x+y) - GameWindow.getInstance().getGamePanel().getHeight()/2;
    setCameraPos(xx, yy);
  }

  public void centerCamera() {
    centerCamera(getPlayerUnit().getPosn());
  }

  /**
   * Draw everything! Start with the floor, then draw all of the objects and
   * units in the order in which they appear in the DepthTree.
   * @param g the Graphics object of the game panel.
   */
  public void drawAll(Graphics g) {
    /*BasicUnit u = getPlayerUnit();
    System.out.printf("%s %s %s, %s\n",
        u.getCurrentActivity(),
        coordsToDir(u.dx, u.dy),
        u.getCurrentAnimation().getIndex(),
        u.getNextActivity());*/
    if (redrawFloor) {
      floor.redraw();
      redrawFloor = false;
    }
    floor.draw(g);
    depthTree.drawAll(g);
  }

  /** Left click stuff - just movement for now (Doesn't include modifiers)
   * @param pixel The (x,y) coordinates of the pixel */
  public void doLeftClick(Posn pixel) {
    Posn posn = pixelToGrid(pixel);
    if (posn == null) return;

    // targeting a unit
    for (Unit v: units) {
      if (posn.equals(v.getPosn())) {
        /*
        for (BasicUnit u: getHumanPlayer().getSelectedUnits()) {
          //u.doUnitInteraction(v);
          u.setNextTargetUnit(v);
        }
        */
        return;
      }
    }
    // targeting an object
    for (GameObject obj: objects) {
      if (posn.equals(obj.getPosn())) {
        if (obj.isInteractable()) {
          // do object interaction stuff
          return;
        } else if (obj.isObstacle()) {
          // do nothing, don't walk into it
          return;
        } else {
          // continue
        }
      }
    }
    // targeting a floor tile
    Unit u = getPlayerUnit();
    u.setNextTargetPosn(posn);
    u.setTargetPosnOverlay(posn);
    u.setNextActivity(Activity.WALKING);
  }

  /**
   * Give an order to the player unit (usually attack).
   * If the targeted tile is not adjacent to the player unit, some kind of
   * pathing stuff will be initiated.
   * @param pixel The mouse coordinates
   * @param activity The ordered activity
   */
  public void doRightClick(Posn pixel, Activity activity) {
    Posn posn = pixelToGrid(pixel);
    Unit u = getPlayerUnit();
    if (pixel == null || posn == null) return;

    // targeting a unit
    for (Unit v: units) {
      if (posn.equals(v.getPosn())) {
        if (v != u) {
          u.setNextTargetUnit(v);
          u.setNextActivity(activity);
          return;
        } else {
          return;
        }
      }
    }
    // Targeting an object - at present there are no interactable objects
    // in game, but eventually there will be things like doors, powerups,
    // etc.
    for (GameObject obj: objects) {
      if (posn.equals(obj.getPosn())) {
        if (obj.isInteractable()) {
          // do object interaction stuff
          return;
        } else if (obj.isObstacle()) {
          // don't walk into it
          return;
        } else {
          // moveBy to it
        }
      }
    }
    // targeting a floor tile
    u.setNextTargetPosn(posn);
    u.setTargetPosnOverlay(posn);
    u.setNextActivity(Activity.WALKING);
  }


  public void doAttackOrder(Posn pixel) {
    doRightClick(pixel, Activity.ATTACKING);
  }

  public void doBashOrder(Posn pixel) {
    doRightClick(pixel, Activity.BASHING);

  }
  // Moves the camera posn; x and y are offsets in pixels (not coords).
  public void moveCamera(int x, int y) {
    setCameraPos(getCameraX() + x, getCameraY() + y);
    redrawFloor = true;
  }

  public void moveCamera(Posn posn) {
    moveCamera(posn.getX(), posn.getY());
  }

  // Add a non-unit object to all relevant lists. 
  public void addObject(GameObject obj) {
    objects.add(obj);
    depthTree.add(obj);
    floor.getTile(obj.getPosn()).getObjects().add(obj);
  }

  // Remove non-unit object from all relevant lists. 
  public void removeObject(GameObject obj) {
    objects.remove(obj);
    depthTree.remove(obj);
    floor.getTile(obj.getPosn()).getObjects().remove(obj);
  }

  // Returns all Posns directly next to the given Posn.
  // Won't return the input posn, and won't return off-map Posns.
  public List<Posn> getAdjacentSquares(Posn p) {
    List<Posn> posns = new ArrayList<>();
    Posn q;
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        if (!((i == 0) && (j == 0))) {
          q = new Posn(p.getX() + i, p.getY() + j);
          if (floor.getTile(q) != null) {
            posns.add(q);
          }
        }
      }
    }
    return posns;
  }

  /**
   * An A* pathfinding attempt.
   * Basically just stealing the pseudocode from
   * http://www.policyalmanac.org/games/aStarTutorial.htm
   * Most of the comments are copy&pasted from that site too.
   * Expect to be optimizing this one for a while, it's fundamental
   * to all of our AI.
   *
   * Notes: p2 is allowed to be an obstacle, since we're using this to path
   * to units/other blocking stuff.
   *
   * TODO: make sure you understand this whole algorithm
   */
  public List<Posn> findPath(Posn p1, Posn p2) {

    // We're not using these HashMaps and I'm kind of concerned. Check on
    // this later.

    //System.out.println("finding path between " + p1 + " and " + p2);
    PathfinderPQ openList = new PathfinderPQ();
    PathfinderPQ closedList = new PathfinderPQ();
    Map<Posn, Integer> costFromCurrent = new HashMap<>();
    Map<Posn, Integer> estCostToEnd = new HashMap<>();
    Map<Posn, Posn> posnParent = new HashMap<>();

    List<Posn> path = new LinkedList<>();
    int dx, dy;
    int f, g, h;
    PathfinderEntry currentEntry;
    Posn currentPosn;
    int currentWeight;
    // 1) Add the starting square (or node) to the open list.
    f = 0;
    costFromCurrent.put(p1, f);
    dx = Math.abs(p2.getX() - p1.getX());
    dy = Math.abs(p2.getY() - p2.getX());
    g = dx + dy;
    estCostToEnd.put(p1, g);
    h = f + g;
    openList.add(new PathfinderEntry(h, p1));
    posnParent.put(p1, p1);
    // 2) Repeat the following:
    // d) Stop when you:

    // Add the target square to the closed list, in which case the path has
    // been found (see note below), or
    // Fail to find the target square, and the open list is empty. In this
    // case, there is no path.   

    while ((closedList.getPosnEntry(p2) == null) && (!openList.isEmpty())) {
      // INSERT SOME KIND OF LOOP HERE
      //   a) Look for the lowest F cost square on the open list. We refer to this as the current square.
      currentEntry = openList.remove();
      currentPosn = currentEntry.getPosn(); // let's hope this is removeMin
      currentWeight = currentEntry.getWeight();
      // b) Switch it to the closed list.
      closedList.add(currentEntry);
      // c) For each of the 8 squares adjacent to this current square ... 
      List<Posn> adjacentSquares = getAdjacentSquares(currentPosn);
      for (int i = 0; i < adjacentSquares.size(); i++) {
        Posn q = adjacentSquares.get(i);
        // If it is not walkable or if it is on the closed list, ignore it.
        // Not handling walking units - let's change that soon?
        if (closedList.getPosnEntry(q) == null) {
          //if (!floor.getTile(q).isBlocked()) {

          // Conditions for a tile being valid:
          // 1) It's not blocked.
          // 2) It's the destination tile - there are some cases where we
          //    don't care if it's blocked.
          // 3) There's a unit blocking it, but this unit is walking.
          boolean addIt = false;
          if (!floor.getTile(q).isBlocked()) {
            addIt = true;
          } else if (q.equals(p2)) {
            addIt = true;
          } else if (floor.getTile(q).getUnit() != null) {
            Unit blockingUnit = floor.getTile(q).getUnit();
            if (floor.getTile(q).isBlocked() && blockingUnit.isMoving()) {
              addIt = true;
            }
          }

          if (addIt) {
            // Otherwise do the following.

            // Make the current square the parent of this square.
            // Record the F, G, and H costs of the square.

            dx = Math.abs(q.getX() - p1.getX());
            dy = Math.abs(q.getY() - p1.getY());
            if (dx+dy == 1) { // straight
              f = currentWeight + 10;
            } else { // diagonal
              f = currentWeight + 2000;
            }
            dx = Math.abs(p2.getX() - q.getX());
            dy = Math.abs(p2.getY() - q.getY());
            g = 10*(dx+dy);
            h = f+g;
            // If it isn�t on the open list, add it to the open list.
            PathfinderEntry e = openList.getPosnEntry(q);
            if (e == null) {
              e = new PathfinderEntry(h, q);
              openList.add(e);
              posnParent.put(q, currentPosn);
    
            // If it is on the open list already, check to see if this path
            // to that square is better, using G cost as the measure.  A lower
            // G cost means that this is a better path. If so, change the parent
            // of the square to the current square, and recalculate the G and F
            // scores of the square. If you are keeping your open list sorted
            // by F score, you may need to resort the list to account for the
            // change.

            } else {
              if (h <= e.getWeight()) {
                openList.remove(e);
                posnParent.remove(e.getPosn());
                openList.add(new PathfinderEntry(h, q));
                posnParent.put(q, currentPosn);
              }
            }
          } else {
            //System.out.println("Blocked tile " + q);
          }
        }
      }
    }
    // No path available. We should probably define a helper method (boolean)
    // to check this - can we do so more efficiently?
    // 3) Save the path. Working backwards from the target square, go from
    // each square to its parent square until you reach the starting square.
    // That is your path.
    if (closedList.getPosnEntry(p2) != null) {
      PathfinderEntry e = closedList.getPosnEntry(p2);
      Posn p = e.getPosn();
      path.add(0, p);
      while (!posnParent.get(p).equals(p)) {
        p = posnParent.get(p);
        path.add(0, p);
      }
      path.remove(0);
      //System.out.println(path);
      if (path.size() == 0) {
        //System.out.println("fux");
      }
      return path;
    } else if (openList.isEmpty()) {
      System.out.println("No path.");
      return null;
    } else {
      System.out.println("What.");
      return null;
    }
  }

  // Helper method for the above.
  public List<Posn> findPath(GameObject x, GameObject y) {
    return findPath(x.getPosn(), y.getPosn());
  }

  public DepthTree getDepthTree() {
    return depthTree;
  }

  public int getCameraX() {
    return cameraPosn.getX();
  }

  public int getCameraY() {
    return cameraPosn.getY();
  }

  public void setCameraPos(Posn posn) {
    cameraPosn = posn;
    redrawFloor = true;
  }

  public void setCameraPos(int x, int y) {
    setCameraPos(new Posn(x,y));
  }

  public HumanPlayer getHumanPlayer() {
    return humanPlayer;
  }

  public Unit getPlayerUnit() {
    return humanPlayer.getUnits().get(0);
  }


  public int getTicks() {
    return ticks;
  }

  public void setTicks(int ticks) {
    this.ticks = ticks;
  }

  public Floor getFloor() {
    return floor;
  }

  public void setFloor(Floor floor) {
    this.floor = floor;
  }

  public Player getPlayer(int index) {
    return players.get(index);
  }

  public List<Unit> getUnits() {
    return units;
  }

  public Random getRNG() {
    return RNG;
  }
  public void doBlockOrder(Posn posn) {
    // Blocking isn't going to moveBy the player unit.
    // Instead, point the player in the direction of the target posn
    // 
    List<Posn> adjacentSquares = getAdjacentSquares(getPlayerUnit().getPosn());
    Posn targetPosn = null;
    for (Posn p: adjacentSquares) {
      //if (!isObstacle(p)) {
      if (targetPosn == null || Utils.distance(p, posn) < Utils.distance(targetPosn, posn)) {
        targetPosn = p;
      }
    }
    //System.out.printf("%s %s\n", posn, targetPosn);
    getPlayerUnit().setNextTargetPosn(targetPosn);
    getPlayerUnit().setNextActivity(Activity.BLOCKING_1);
  }

  public boolean isObstacle(Posn p) {
    return getFloor().getTile(p).isBlocked();
  }

  public void openLevel() {
    Level level = levels.get(levelIndex);
    if (level != null) {
      level.init();
      floor = level.getFloor();
      redrawFloor = true;
      depthTree.clear();
      units.clear();
      for (Unit u: level.getUnits()) {
        depthTree.add(u.getFloorOverlay());
        if (u.getTargetPosnOverlay() != null) {
          System.out.println("zomg");
        }
        addUnit(u);
        // This is a dumb hack to correct the depth tree.
        u.moveTo(u.getPosn());
      }
      objects.clear();
      for (GameObject o: level.getObjects()) {
        addObject(o);
      }
      centerCamera();
    }
  }

  public void playSound(String string) {
    soundPlayer.playSoundThread(string);
  }

  /**
   * I'm not crazy about this solution, but there was a concurrent modification
   * exception thing happening.  Basically we add the unit to the deadUnits
   * array list now, and then after we finish iterating through we'll remove it
   * from both.
   * @param unit
   */
  public void queueRemoveUnit(Unit unit) {
    unitsToRemove.add(unit);
  }

  public void queueAddUnit(Unit unit) {
    unitsToAdd.add(unit);
  }

  public int nextEnemyID() {
    // how does ++ work
    return nextEnemyID++;
  }

  public List<GameObject> getObjects() {
    return objects;
  }

  public void loadLevels() {
    levels.clear();
    levels.add(new TestLevel());
    //levels.add(new WizardLevel(this));
    levelIndex = 0;
  }

  public InputHandler getInputHandler() {
    return inputHandler;
  }

  public void killAllEnemies() {
    for (Unit u : units) {
      if (u.isHostile(getPlayerUnit())) {
        u.die();
        queueRemoveUnit(u);
      }
    }
  }

  /**
   * By using this as a Singleton, we can avoid having to constantly pass it around as a parameter and store
   * references to it.
   */
  public static RPG getInstance() {
    if (instance == null) {
      instance = new RPG();
    }
    return instance;
  }
}
