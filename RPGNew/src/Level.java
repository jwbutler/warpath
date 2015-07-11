import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import jwbgl.Posn;
import jwbgl.Surface;

public class Level {
  protected Floor floor;
  protected ArrayList<Unit> units;
  protected ArrayList<GameObject> objects;
  private final Color GRASS_COLOR = new Color(0,255,0);
  private final Color STONE_COLOR = new Color(128,128,128);
  private final Color TREE_COLOR = new Color(0,128,0);
  private final Color WALL_COLOR = new Color(192,192,192);
  private final Color ENEMY_COLOR = new Color(255,128,64);
  private final Color PLAYER_COLOR = new Color(255,0,0);
  public Level(RPG game, int width, int height) {
    init(game, width, height);
  }
  public Level(RPG game, String filename) {
    //super(game);
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
        } else if (c.equals(ENEMY_COLOR)) {
          units.add(new EnemySwordGuy(game, "", new Posn(x,y), game.getPlayer(2)));
        } else if (c.equals(PLAYER_COLOR)) {
          game.getPlayerUnit().setPosn(new Posn(x,y));
          units.add(game.getPlayerUnit());
        }
      }
    }
    
  }
  /* Had to split this off because the restrictions on super(). */
  public void init(RPG game, int width, int height) {
    floor = new Floor(game, width, height);
    units = new ArrayList<Unit>();
    objects = new ArrayList<GameObject>();
  }
  public ArrayList<Unit> getUnits() { return units; }
  public ArrayList<GameObject> getObjects() { return objects; }
  public Floor getFloor() { return floor; }
}
