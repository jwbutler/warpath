import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

import jwbgl.*;

/* The main game engine.  Expect this one to be a few thousand lines long.
 * 
 * ===== CHANGELOG =====
 * 8/16 - New version! Rewriting for single character combat.
 * 5/30 - lots of comments.
 * 5/26 - Modified pathfinding code to allow for blocked destination, and to
 *        handle walking units in the path.
 *      - Added addOverlay() 
 *      - Started unit interaction stuff
 * 5/23 - findPath() added
 *      - getAdjacentSquares() added
 * 1/29 - Commented stuffs.
 * =====================
 */
  
public class RPG implements ActionListener {
	
  RPGDriver driver;
  
  private Timer frameTimer;
  private int ticks;
  private int nextEnemyID;
  private Floor floor;
  
  // Using a HashTable for this is pretty strange.  But it lets us access
  // players by number, which I think is useful.
  private Hashtable<Integer, Player> players;
  private ArrayList<Unit> units;
  private ArrayList<GameObject> objects; // Non-units
  private ArrayList<Unit> deadUnits;
  
  // Unsure if we should keep this reference, he's always going to be
  // player 1, right?
  private HumanPlayer humanPlayer;

  private DepthTree depthTree;
  private Posn cameraPosn;
  private Random RNG;
  
  private GameWindow gameWindow;
  private SoundPlayer soundPlayer;
  
  // Constants
  public static final int FPS = 15; // should be 20
  public static final int TILE_WIDTH = 96;
  public static final int TILE_HEIGHT = 48;
  public static final int CAMERA_INCREMENT_X = 48;
  public static final int CAMERA_INCREMENT_Y = 24;
  public static final int HUD_PANEL_HEIGHT = 100;
  public static final String[] DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
  public static final Color TRANSPARENT_WHITE = new Color(0x00FFFFFF, true);
  private boolean redrawFloor;

  public RPG(GameWindow gameWindow) {
    this.gameWindow = gameWindow;
    floor = new Floor(this, 15, 15);
    frameTimer = new Timer(1000/FPS, this);
    ticks = 0;
    nextEnemyID = 1;
    players = new Hashtable<Integer, Player>();
    addHumanPlayer();
    units = new ArrayList<Unit>();
    objects = new ArrayList<GameObject>();
    deadUnits = new ArrayList<Unit>();
    RNG = new Random();
    soundPlayer = new SoundPlayer();
    //playSound("crystal.wav");
    
    // Do we need to extend the JFrame class? I'm thinking no.
    //gameWindow = new JFrame();
    //gameWindow.getContentPane().setLayout(null);
    //gameWindow.add(gamePanel);
    
    // Make the Char Creator
    //getContentPane().setLayout(null);
    //getContentPane().add(gamePanel); 
	
    depthTree = new DepthTree();
    cameraPosn = null;
    redrawFloor = true;

  }
  
  // Start the timer.  We might also use this to restart/unpause
  public void start(HashMap<Color, Color> swaps) {
    // Add some player units.
    //HumanUnit u = new HumanUnit(me, "u", new Posn(3,4), me.getHumanPlayer());
    
    // This is a dumb workaround.
    setFloor(new Floor(this, 1,1));
    getFloor().setTile(0,0, new Tile(this, new Posn(0,0), "tile_96x48_grass.png"));
    SwordGuy u = new SwordGuy(this, "u", new Posn(0,0), getHumanPlayer(), swaps);
    //SwordGirl u = new SwordGirl(me, "u", new Posn(3,4), me.getHumanPlayer());
    addUnit(u);
    
    // Make a hostile AI player
    addPlayer(2, new AIPlayer());
    getHumanPlayer().setHostile(getPlayer(2));
    getPlayer(2).setHostile(getPlayer(1));
    
    Level testLevel = new TestLevel(this);
    openLevel(testLevel);
    frameTimer.start();
    centerCamera();
  }
  public void start() {
    start(new HashMap<Color, Color>());
  }
  
  // Called every time the frame timer fires.
  // Redraws everything, then increments the tick counter.
  // IS THIS THE RIGHT ORDER OF OPERATIONS?
  public void actionPerformed(ActionEvent e) {
  
    /* Do blocking event code. */
    String currentActivity = getPlayerUnit().getCurrentActivity();
    String nextActivity = getPlayerUnit().getNextActivity();
    if (ctrlIsDown()) {
      if (currentActivity.equals("standing") || currentActivity.equals("walking")) {
        if (nextActivity != null && nextActivity.equals("bashing")) {
          /* Bash */
        } else {
          if (pixelToGrid(getMousePosn()) != null) {
            if (getPlayerUnit().currentEP > 0) {
              getPlayerUnit().setNextActivity("blocking_1");
              getPlayerUnit().setNextTargetPosn(pixelToGrid(getMousePosn()));
              getPlayerUnit().setTargetPosnOverlay(null);
            }
          }
        }
      } else if (currentActivity.equals("blocking_1") || currentActivity.equals("blocking_2")) {
        if (pixelToGrid(getMousePosn()) != null) {
          getPlayerUnit().setNextTargetPosn(pixelToGrid(getMousePosn()));
          getPlayerUnit().setTargetPosnOverlay(null);
        }
      } else if (currentActivity.equals("bashing") || currentActivity.equals("attacking")) {
        if (pixelToGrid(getMousePosn()) != null) {
          getPlayerUnit().setNextActivity("blocking_1");
          getPlayerUnit().setNextTargetPosn(pixelToGrid(getMousePosn()));
          getPlayerUnit().setTargetPosnOverlay(null);
        }
      }
    } else {
      /*if (currentActivity.equals("blocking_2")) {
        if (getPlayerUnit().getNextActivity() != null && getPlayerUnit().getNextActivity().equals("bashing")) {
          System.out.println("bash?");
        } else {
          //getPlayerUnit().setNextActivity("blocking_3");
          getPlayerUnit().setNextActivity(null);
        }
      } else */
      if (getPlayerUnit().getNextActivity() != null) {
        if (getPlayerUnit().getNextActivity().equals("blocking_1") || getPlayerUnit().getNextActivity().equals("blocking_2")) {
          getPlayerUnit().setNextActivity(null);
        }
      }
    }
    
    for (Unit u: units) {
      u.doUpkeep();
    }

    for (Unit u: units) {
      u.doEvents();
    }
    
    /* Kill units that have been queued for death. (This happens during
     * nextActivity(), which happens during doUpkeep()... */
    for (Unit u : deadUnits) {
      removeUnit(u);
    }
    deadUnits.clear();
    // Repaint() includes a call to drawAll().
    gameWindow.repaint();
    incrementTicks();
  }

  // Make the human player and return it.  I'm not sure we need this.
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
  
  // Add a player with the specified player number.
  // The whole playerNumber field is oddly handled.  Whatever.
  public void addPlayer(int playerNumber, Player player) {
    players.put(playerNumber, player);
  }
  
  /* Add the specified unit to all relevant lists.
   * Note that the unit is initialized with its controller established.
   * (I forget why :( ) */
  public void addUnit(Unit u) {
    units.add(u);
    u.getPlayer().getUnits().add(u);
    if (u.getPlayer().equals(getHumanPlayer())) {
      gameWindow.getHudPanel().init();
    }
    depthTree.add(u);
    floor.getTile(u.getX(),u.getY()).setUnit(u);
    u.updateFloorOverlay();
  }
  
  /* Removes the specified unit from all relevant lists.
     Specifically, each floor tile's list of units, and the RPG's units list,
     as well as the RPG's depth tree.
     Also removes its floor overlay from all relevant lists.
     I don't think we need to check contains(), but it feels wrong...
  */
  public void removeUnit(Unit u) {
    units.remove(u);
    depthTree.remove(u);
    removeObject(u.getFloorOverlay());

    floor.getTile(u.getX(),u.getY()).setUnit(null);
    for (int i = 1; i < players.size()+1; i++) {
      Player p = players.get(i);
      if (p.getUnits().contains(u)) {
        p.getUnits().remove(u);
      }
    }
    
    for (int i = 0; i < units.size(); i++) {
      if (units.get(i).getTargetUnit() != null && units.get(i).getTargetUnit().equals(u)) {
        units.get(i).setCurrentActivity("standing");
        units.get(i).setTargetUnit(null);
        units.get(i).setTargetPosn(null);
      }
    }
  }
  
  /** 
   * This returns the upper left corner of the grid tile.  Note that the
   * origin (0,0) is actually inside the menu bar; the playable area is
   * smaller than it "should" be
   * @param posn - the x and y coordinates of the grid tile

   */
  public Posn gridToPixel(Posn posn) {
    
    int left, top;
    // Offsets are to make it so that pixel (0,0) is at the very top left of the floor
    int xOffset = floor.height * TILE_WIDTH / 2;
    int yOffset = 0;
    //System.out.println("offsets " + xOffset + " " + yOffset);
    int x = posn.getX();
    int y = posn.getY();
    left = (x * TILE_WIDTH / 2) - (y * TILE_WIDTH / 2) - getCameraX() + xOffset;
    top = (x * TILE_HEIGHT / 2) + (y * TILE_HEIGHT / 2) - getCameraY() - yOffset;
    return new Posn(left, top);
  }
  
  public Posn gridToPixel(int x, int y) {
    return gridToPixel(new Posn(x,y));
  }
  
  /** Finds the nearest tile to a given pixel.  Returns null if it's outside
   * the map.
   * It works by checking EVERY floor tile, finding the coordinates at which
   * it's being drawn, and checking the transparency at the given point.
     @param pixel
     The (x,y) coordinates of the pixel */
  /* TODO If the coordinates are off the grid, find the closest valid grid
   * tile instead of returning null. */
  public Posn pixelToGrid(Posn pixel) {
    // We need to restrict the range of X and Y, but... later
    for (int y = 0; y <= floor.height; y++) {
      for (int x = 0; x <= floor.width; x++) {
        Posn tilePixel = gridToPixel(x, y);
        int left = tilePixel.getX();
        int top = tilePixel.getY();
        Rect tileRect = new Rect(left, top, TILE_WIDTH, TILE_HEIGHT);
        if (tileRect.collidePoint(pixel.getX(), pixel.getY())) {
          int tileX = pixel.getX() - left;
          int tileY = pixel.getY() - top;
          int rgba = floor.getTile(x, y).getSurface().getRGB(tileX, tileY);
          Color c = new Color(rgba, true);
          if (c.getAlpha() == 255) {
            return new Posn(x,y);
          }
        }
      }
    }
    return null;
  }
  
  public Posn pixelToGrid(int x, int y) {
    return pixelToGrid(new Posn(x,y));
  }
  
  //Centers the camera on a given posn.
  public void centerCamera(Posn p) {
    int x = p.getX();
    int y = p.getY();
    int xx = TILE_WIDTH/2 * (x + (floor.height-y)) - gameWindow.getGamePanel().getWidth()/2;
    int yy = TILE_HEIGHT/2 * (x+y) - gameWindow.getGamePanel().getHeight()/2;
    setCameraPos(xx, yy);
  }

  public void centerCamera() {
    centerCamera(getPlayerUnit().getPosn());
  }
  
  /** Draw everything! Start with the floor, then draw all of the objects and
   * units in the order in which they appear in the DepthTree.
   * @param g the Graphics object of the game panel.
   */
  public void drawAll(Graphics g) {
    /*Unit u = getPlayerUnit();
    System.out.printf("%s %s %s, %s\n",
        u.getCurrentActivity(),
        coordsToDir(u.dx, u.dy),
        u.getCurrentAnimation().getIndex(),
        u.getNextActivity());*/
    if (redrawFloor) {
      floor.redraw(this);
      redrawFloor = false;
    }
    floor.draw(g);
    depthTree.drawAll(g);
  }
  
  public double distance(Posn p, Posn q) { 
    int dx = Math.abs(q.getX() - p.getX());
    int dy = Math.abs(q.getY() - p.getY());
    return Math.sqrt(dx*dx + dy*dy);
  }
  
  public double distance(GameObject p, GameObject q) { 
    double dx = Math.abs(q.getX() - p.getX());
    double dy = Math.abs(q.getY() - p.getY());
    return Math.sqrt(dx*dx + dy*dy);
  }
  
  /** Calculate the distance between two points.
   * IMPORTANT: not Pythagorean distance, but Civ-style (I think) distance.
   * Returns the larger of the x-distance and the y-distance.
   * @param p the first point
   * @param q the second point
   */
  public int distance2(Posn p, Posn q) {
    int dx = Math.abs(q.getX() - p.getX());
    int dy = Math.abs(q.getY() - p.getY());
    return Math.max(dx, dy);
  }
  
  public int distance2(GameObject x, GameObject y) {
    return distance2(x.getPosn(), y.getPosn());
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
        for (Unit u: getHumanPlayer().getSelectedUnits()) {
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
    u.setNextActivity("walking");
  }
  
  /** Give an order to the player unit (usually attack).
   * If the targeted tile is not adjacent to the player unit, some kind of
   * pathing stuff will be initiated.
   * @param pixel The mouse coordinates
   * @param activity The ordered activity 
   *  */
  public void doRightClick(Posn pixel, String activity) {
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
          // move to it
        }
      }
    }
    // targeting a floor tile
    u.setNextTargetPosn(posn);
    u.setTargetPosnOverlay(posn);
    u.setNextActivity("walking");
  }
  
  
  public void doAttackOrder(Posn pixel) {
    doRightClick(pixel, "attacking");
  }

  public void doBashOrder(Posn pixel) {
    doRightClick(pixel, "bashing");
    
  }
  // Moves the camera posn; x and y are offsets in pixels (not coords).
  public void moveCamera(int x, int y) {
    setCameraPos(getCameraX() + x, getCameraY() + y);
    redrawFloor = true;
  }
  
  public void moveCamera(Posn posn) {
    moveCamera(posn.getX(), posn.getY());
  }
  
  /* Given an (x,y) pair, returns the corresponding compass direction. */ 
  public String coordsToDir(int x, int y) {
    String rtn = "";
    if (y == -1) {
      rtn = "N";
    } else if (y == 1) {
      rtn = "S";
    }
    if (x == -1) {
      rtn += "W";
    } else if (x == 1) {
      rtn+= "E";
    }
    if (rtn.equals("")) {
      return null; 
    } else {
      return rtn;
    }
  }
  
  // Helper method.
  public String coordsToDir(Posn posn) {
    return coordsToDir(posn.getX(), posn.getY());
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
  public ArrayList<Posn> getAdjacentSquares(Posn p) {
    ArrayList<Posn> posns = new ArrayList<Posn>();
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
  
  /* An A* pathfinding attempt.
   * Basically just stealing the pseudocode from 
   * http://www.policyalmanac.org/games/aStarTutorial.htm
   * Most of the comments are copy&pasted from that site too.
   * Expect to be optimizing this one for a while, it's fundamental
   * to all of our AI.
   * 
   * Notes: p2 is allowed to be an obstacle, since we're using this to path
   * to units/other blocking stuff. */
  public LinkedList<Posn> findPath(Posn p1, Posn p2) {
  
    // We're not using these hashtables and I'm kind of concerned. Check on
    // this later.

    //System.out.println("finding path between " + p1 + " and " + p2);
    PathfinderPQ openList = new PathfinderPQ();
    PathfinderPQ closedList = new PathfinderPQ();
    Hashtable<Posn, Integer> costFromCurrent = new Hashtable<Posn, Integer>();
    Hashtable<Posn, Integer> estCostToEnd = new Hashtable<Posn, Integer>();
    Hashtable<Posn, Posn> posnParent = new Hashtable<Posn, Posn>();
    
    LinkedList<Posn> path = new LinkedList<Posn>();
    int dx, dy;
    int f, g, h;
    PathfinderEntry currentEntry;
    Posn currentPosn;
    int currentWeight;
    ArrayList<Posn> adjacentSquares = new ArrayList<Posn>(); 
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
      adjacentSquares = getAdjacentSquares(currentPosn);
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
          }
          if (q.equals(p2)) {
            addIt = true;
          }
          if (floor.getTile(q).getUnit() != null) {
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
            // If it isn’t on the open list, add it to the open list.
            PathfinderEntry e = openList.getPosnEntry(q);
            if (e == null) {
              e = new PathfinderEntry(h, q);
              openList.add(e);
              posnParent.put(q, currentPosn);
    
            /* If it is on the open list already, check to see if this path
             * to that square is better, using G cost as the measure.  A lower
             * G cost means that this is a better path. If so, change the parent
             * of the square to the current square, and recalculate the G and F
             * scores of the square. If you are keeping your open list sorted
             * by F score, you may need to resort the list to account for the
             * change. */
    
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
    /* No path available. We should probably define a helper method (boolean)
     * to check this - can we do so more efficiently?
     * 3) Save the path. Working backwards from the target square, go from
     * each square to its parent square until you reach the starting square.
     * That is your path. */ 
    if (closedList.getPosnEntry(p2) != null) {
      PathfinderEntry e = closedList.getPosnEntry(p2);
      Posn p = e.getPosn();
      path.addFirst(p);
      while (!posnParent.get(p).equals(p)) {
        p = posnParent.get(p);
        path.addFirst(p);
      }
      path.removeFirst();
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
  public LinkedList<Posn> findPath(GameObject x, GameObject y) {
    return findPath(x.getPosn(), y.getPosn());
  }

  // ===== ACCESSOR METHODS =====
  
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
  
  public ArrayList<Unit> getUnits() {
    return units;
  }
  public void setUnits(ArrayList<Unit> units) {
    this.units = units;
  }
  
  public Random getRNG() {
    return RNG;
  }
  public void doBlockOrder(Posn posn) {
    // Blocking isn't going to move the player unit.
    // Instead, point the player in the direction of the target posn
    // 
    ArrayList<Posn> adjacentSquares = getAdjacentSquares(getPlayerUnit().getPosn());
    Posn targetPosn = null;
    for (Posn p: adjacentSquares) {
      //if (!isObstacle(p)) {
      if (targetPosn == null || distance(p, posn) < distance(targetPosn, posn)) {
        targetPosn = p;
      }
    }
    //System.out.printf("%s %s\n", posn, targetPosn);
    getPlayerUnit().setNextTargetPosn(targetPosn);
    getPlayerUnit().setNextActivity("blocking_1");
  }
  
  public boolean ctrlIsDown() {
    return gameWindow.getGamePanel().ctrlIsDown;
  }
  
  public boolean shiftIsDown() {
    return gameWindow.getGamePanel().shiftIsDown;
  }
  
  public boolean isObstacle(Posn p) {
    return getFloor().getTile(p).isObstacle();
  }

  public Posn getMousePosn() {
    return gameWindow.getGamePanel().getMousePosn();
  }

  public JFrame getGameWindow() {
    return gameWindow;
  }
  
  public void openLevel(Level level) {
    floor = level.getFloor();
    depthTree = new DepthTree();
    units = new ArrayList<Unit>();
    for (Unit u: level.getUnits()) {
      addUnit(u);
    }
    objects = new ArrayList<GameObject>();
    for (GameObject o: level.getObjects()) {
      addObject(o);
    }
  }

  public void playSound(String string) {
    soundPlayer.playSoundThread(string);
  }

  /* I'm not crazy about this solution, but there was a concurrent modification
   * exception thing happening.  Basically we add the unit to the deadUnits
   * array list now, and then after we finish iterating through we'll remove it
   * from both. */
  public void queueRemoveUnit(Unit unit) {
    deadUnits.add(unit);
  }

  public int nextEnemyID() {
    // how does ++ work
    return nextEnemyID++;
  }

}
