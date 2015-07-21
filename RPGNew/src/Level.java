import java.awt.Color;
import java.util.ArrayList;

import jwbgl.Posn;
import jwbgl.Surface;

public class Level {
  protected Floor floor;
  protected RPG game;
  protected ArrayList<Unit> units;
  protected ArrayList<GameObject> objects;
  private final Color GRASS_COLOR = new Color(0,255,0);
  private final Color STONE_COLOR = new Color(128,128,128);
  private final Color TREE_COLOR = new Color(0,128,0);
  private final Color WALL_COLOR = new Color(192,192,192);
  private final Color WIZARD_COLOR = new Color(128,128,0);
  private final Color ZOMBIE_COLOR = new Color(255,128,64);
  private final Color CORPSE_COLOR = new Color(64,64,0);
  private final Color PLAYER_COLOR = new Color(255,0,0);
  public Level(RPG game, int width, int height) {
    init(game, width, height);
  }
  public Level(RPG game, String filename) {
    Surface img = new Surface(filename);
    init(game, img.getWidth(), img.getHeight());
    for (int y=0; y<img.getHeight(); y++) {
      for (int x=0; x<img.getWidth(); x++) {
        int rgb = img.getRGB(x,y);
        Color c = new Color(rgb);
        Tile t;
        if (c.equals(GRASS_COLOR)) {
          t = new Tile(game, new Posn(x,y), "tile_48x24_grass.png");
        } else if (c.equals(STONE_COLOR)) {
          t = new Tile(game, new Posn(x,y), "tile_48x24_stone.png");
        } else {
          /* Check all the surrounding tiles.  Take a consensus for the underlying tile color. */
          int numGrass=0, numStone=0;
          for (int j=y-1; j<=y+1; j++) {
            for (int i=x-1; i<=x+1; i++) {
              if (i>=0 && i<img.getWidth() && j>=0 && j<img.getHeight() && !(i==x && j==y)) {
                int rgb2 = img.getRGB(i,j);
                Color c2 = new Color(rgb2);
                if (c2.equals(GRASS_COLOR)) numGrass++;
                else if (c2.equals(STONE_COLOR)) numStone++;
              }
            }
          }
          if (numGrass >= numStone) {
            t = new Tile(game, new Posn(x,y), "tile_48x24_grass.png");
          } else {
            t = new Tile(game, new Posn(x,y), "tile_48x24_stone.png");
          }
        }
        floor.setTile(x, y, t);
        
        if (c.equals(TREE_COLOR)) {
          //objects.add(new GameObject(x,y))
        } else if (c.equals(WALL_COLOR)) {
          objects.add(new Wall(game, new Posn(x,y), "wall_48x78_1.png"));
        } else if (c.equals(WIZARD_COLOR)) {
          addWizard(x,y);
          //units.add(new EnemyZombie(game, "", new Posn(x,y), game.getPlayer(2)));
          //units.add(new EnemyRobedWizard(game, "", new Posn(x,y), game.getPlayer(2)));
        } else if (c.equals(ZOMBIE_COLOR)) {
          //units.add(new EnemySwordGuy(game, "", new Posn(x,y), game.getPlayer(2)));
          addZombie(x,y);
          //units.add(new EnemyRobedWizard(game, "", new Posn(x,y), game.getPlayer(2)));
        } else if (c.equals(CORPSE_COLOR)) {
          addCorpse(x,y);
        } else if (c.equals(PLAYER_COLOR)) {
          game.getPlayerUnit().setPosn(new Posn(x,y));
          units.add(game.getPlayerUnit());
        }
      }
    }
    
  }
  /* Had to split this off because the restrictions on this(). */
  public void init(RPG game, int width, int height) {
    this.game = game;
    floor = new Floor(game, width, height);
    units = new ArrayList<Unit>();
    objects = new ArrayList<GameObject>();
  }
  
  /* Override this to set enemy types. We can add more methods e.g. addBossEnemy(), addSuperEnemy(). */
  protected void addZombie(int x, int y) {
    units.add(new EnemyZombie(game, String.format("Zombie %d", game.nextEnemyID()), new Posn(x,y), game.getPlayer(2)));
  }
  protected void addWizard(int x, int y) {
    units.add(new EnemyRobedWizard(game, String.format("Wizard %d", game.nextEnemyID()), new Posn(x,y), game.getPlayer(2)));
  }
  protected void addCorpse(int x, int y) {
    if (game.getRNG().nextBoolean()) {
      objects.add(new Corpse(game, new Posn(x,y), "player_falling_NE_4.png"));
    } else {
      objects.add(new Corpse(game, new Posn(x,y), "player_falling_S_4.png"));
    }
  }
  
  public ArrayList<Unit> getUnits() { return units; }
  public ArrayList<GameObject> getObjects() { return objects; }
  public Floor getFloor() { return floor; }
}
