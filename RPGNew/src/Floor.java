import java.awt.Graphics;

import jwbgl.*;
/* Pretty much just a 2d array of Tiles. We draw all the tiles onto an
 * intermediate draw surface rather than drawing them individually.
 * We don't redraw the floor every frame, just when stuff moves basically.
 * We should look into only redrawing the tiles we need to redraw. */

public class Floor {

  private RPG game;
  private Tile[][] tiles;
  private Surface floorSurface;
  private int width, height; // number of grid tiles, NOT pixels
  
  public Floor(RPG game, int width, int height) {
    this.game = game;
    this.width = width;
    this.height = height;
    tiles = new Tile[this.width][this.height];
    /* We're going to set these manually now. */
    /*for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        tiles[x][y] = new Tile(game, new Posn(x,y), "tile_48x24_stone.png");
      }
    }*/
    floorSurface = new Surface(game.getGameWindow().getWidth(), game.getGameWindow().getHeight());
  }

  public void redraw(RPG game) {
    Graphics g = floorSurface.getGraphics();
    g.clearRect(0, 0, floorSurface.getWidth(), floorSurface.getHeight());
    
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        Posn pixel = game.gridToPixel(x, y);
        tiles[x][y].getSurface().draw(g, pixel);
        //Rect screenRect = game.getScreenRect();
      }
    }
  }

  public void draw(Graphics g) {
    // Offsets have not been figured out yet, need camera shit
    floorSurface.draw(g, 0, 0);
  }
  
  public Tile getTile(int x, int y) {
    try {
      return tiles[x][y];
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }
  
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

  public boolean contains(Posn p) {
    return (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height); 
  }
  
}
