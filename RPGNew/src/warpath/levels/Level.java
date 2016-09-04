package warpath.levels;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jwbgl.Posn;
import jwbgl.Surface;
import warpath.core.RPG;
import warpath.objects.Corpse;
import warpath.objects.Floor;
import warpath.objects.GameObject;
import warpath.objects.Tile;
import warpath.objects.Wall;
import warpath.units.EnemyRobedWizard;
import warpath.units.EnemySwordGuy;
import warpath.units.EnemyZombie;
import warpath.units.Unit;

/**
 * Represents a level.  Initialized from a bitmap where colors represent
 * different types of tile, objects and units.  Contains constants
 * representing these colors.
 */
public abstract class Level {
  protected final List<Unit> units;
  protected final List<GameObject> objects;
  private final Surface img;
  
  private final static Color GRASS_COLOR = new Color(0,255,0);
  private final static Color STONE_COLOR = new Color(128,128,128);
  private final static Color TREE_COLOR = new Color(0,128,0);
  private final static Color WALL_COLOR = new Color(192,192,192);
  private final static Color BANDIT_COLOR = new Color(255,128,64);
  private final static Color WIZARD_COLOR = new Color(128,128,0);
  private final static Color ZOMBIE_COLOR = new Color(255,255,0);
  private final static Color PLAYER_COLOR = new Color(255,0,0);
  
  protected Floor floor;
  
  public Level(String filename) {
    img = new Surface(filename);
    units = new ArrayList<>();
    objects = new ArrayList<>();
  }

  /**
   * Initializes the map.  This should rightly be part of the constructor, but
   * I Had to split this off because we don't actually load this until
   * the level is started in game.
   */
  public void init() {
    int width = img.getWidth();
    int height = img.getHeight();
    RPG game = RPG.getInstance();
    floor = new Floor(width, height);
    // Populate tiles, units and objects from the contents of the map filename.
    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        int rgb = img.getRGB(x,y);
        Color c = new Color(rgb);
        Tile t;
        if (c.equals(GRASS_COLOR)) {
          t = new Tile(new Posn(x,y), "tile_48x24_grass.png");
        } else if (c.equals(STONE_COLOR)) {
          t = new Tile(new Posn(x,y), "tile_48x24_stone.png");
        } else {
          // Check all the surrounding tiles.  Take a consensus for the
          // underlying tile color.
          // TODO Design something more robust, or at least update this when
          // you introduce new tile types.
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
            t = new Tile(new Posn(x,y), "tile_48x24_grass.png");
          } else {
            t = new Tile(new Posn(x,y), "tile_48x24_stone.png");
          }
        }
        floor.setTile(x, y, t);
        
        // Add objects and units.
        if (c.equals(TREE_COLOR)) {
          //objects.add(new GameObject(x,y))
        } else if (c.equals(WALL_COLOR)) {
          objects.add(new Wall(new Posn(x,y), "wall_48x78_1.png"));
        } else if (c.equals(WIZARD_COLOR)) {
          addWizard(x,y);
        } else if (c.equals(ZOMBIE_COLOR)) {
          addZombie(x,y);
        } else if (c.equals(BANDIT_COLOR)) {
          addBandit(x,y);
        } else if (c.equals(PLAYER_COLOR)) {
          game.getPlayerUnit().setPosn(new Posn(x,y));
          units.add(game.getPlayerUnit());
        }
      }
    }
  }
  
  protected void addZombie(int x, int y) {
    RPG game = RPG.getInstance();
    units.add(new EnemyZombie(String.format("Zombie %d", game.nextEnemyID()), new Posn(x,y), game.getPlayer(2)));
  }
  
  protected void addWizard(int x, int y) {
    RPG game = RPG.getInstance();
    units.add(new EnemyRobedWizard(String.format("Wizard %d", game.nextEnemyID()), new Posn(x,y), game.getPlayer(2)));
  }
  
  protected void addBandit(int x, int y) {
    RPG game = RPG.getInstance();
    units.add(new EnemySwordGuy(String.format("Bandit %d", game.nextEnemyID()), new Posn(x,y), game.getPlayer(2)));
  }
  
  protected void addCorpse(int x, int y) {
    RPG game = RPG.getInstance();
    if (game.getRNG().nextBoolean()) {
      objects.add(new Corpse(new Posn(x,y), "player_falling_NE_4.png"));
    } else {
      objects.add(new Corpse(new Posn(x,y), "player_falling_S_4.png"));
    }
  }
  
  /**
   * Placeholder.
   */
  public boolean checkVictory() {
    RPG game = RPG.getInstance();
    return(game.getPlayer(2).getUnits().size() == 0);
  }
  
  public List<Unit> getUnits() {
    return units;
  }
  public List<GameObject> getObjects() {
    return objects;
  }
  public Floor getFloor() {
    return floor;
  }
}
