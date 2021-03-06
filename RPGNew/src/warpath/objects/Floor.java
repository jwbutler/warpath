package warpath.objects;
import java.awt.Graphics;

import jwbgl.*;
import warpath.core.RPG;
import warpath.ui.GameWindow;

public class Floor {
  private final Tile[][] tiles;
  private final int width, height; // number of grid tiles, NOT pixels
  private Surface floorSurface;
  
  public Floor(int width, int height) {
    this.width = width;
    this.height = height;
    tiles = new Tile[this.width][this.height];
    GameWindow gameWindow = GameWindow.getInstance();
    floorSurface = new Surface(gameWindow.getWidth(), gameWindow.getHeight());
  }

  /**
   * Refreshes the image of the floor.  Does not actually render it onto the
   * screen.
   */
  public void redraw() {
    Graphics g = floorSurface.getGraphics();
    g.clearRect(0, 0, floorSurface.getWidth(), floorSurface.getHeight());
    RPG game = RPG.getInstance();
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        Posn pixel = game.gridToPixel(x, y);
        tiles[x][y].getSurface().draw(g, pixel);
      }
    }
  }

  /** Draws the floor surface onto the screen.
   * @param g - the AWT Graphics object of the game panel. */
  public void draw(Graphics g) {
    // Offsets have not been figured out yet, need camera shit
    floorSurface.draw(g, 0, 0);
  }
  
  /** Returns the tile with coordinates (x,y).
   * @param x - the x coordinate of the tile
   * @param y - the y coordinate of the tile
   * @return the tile at (x,y), or null if there is no such tile */
  public Tile getTile(int x, int y) {
    try {
      return tiles[x][y];
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }
  
  /** Returns the tile with coordinates (x,y).
   * @param p - the Posn at which the tile is located
   * @return the tile at p, or null if there is no such tile */
  public Tile getTile(Posn p) {
    return getTile(p.getX(), p.getY());
  }
  
  public void setTile(int x, int y, Tile tile) {
    tiles[x][y] = tile;
  }

  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }

  /** Returns true if p is within the bounds of the floor.
   * TODO This needs to be updated for non-rectangular floors.
   * @param p - the Posn to test
   * @return whether p is part of the floor */
  public boolean contains(Posn p) {
    return (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height); 
  }
  
}
