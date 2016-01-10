package warpath.objects;
import java.awt.Graphics;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPG;

/** This class contains basic methods to be used by all sorts of game objects,
 * units or otherwise.  We'll be subclassing it a lot. */

public abstract class BasicObject implements GameObject {
  private Posn posn;
  protected int xOffset, yOffset;
  protected int depthOffset;
  private int depth;
  private Rect rect;
  protected Surface surface;
  protected RPG game;
  
  public BasicObject(RPG game, Posn posn) {
    this.game = game;
    this.posn = posn;
    depthOffset = 0;
    xOffset = yOffset = 0;
    updateDepth();
  }

  @Override
  public Surface getSurface() {
    return surface;
  }

  @Override
  public Rect getRect() {
    return rect;
  }
  
  public int getX() {
    return posn.getX();
  }

  public int getY() {
    return posn.getY();
  }
  
  public Posn getPosn() {
    return posn;
  }
  
  public void setPosn(Posn p) {
    posn = p;
  }
  
  public int getDepth() {
    return depth;
  }

  public int getxOffset() {
    return xOffset;
  }

  public void setxOffset(int xOffset) {
    this.xOffset = xOffset;
  }

  public int getyOffset() {
    return yOffset;
  }

  public void setyOffset(int yOffset) {
    this.yOffset = yOffset;
  }
  
  /** Draws the object onto the game panel using the specified offsets.
   * @param g - the AWT graphics object used to render it
   */
  public void draw(Graphics g) {
    // Offsets have not been figured out yet, need camera shit
    Posn pixel = game.gridToPixel(posn); // returns top left
    int left = pixel.getX() + Constants.TILE_WIDTH/2 - getSurface().getWidth()/2 + xOffset;
    int top = pixel.getY() + Constants.TILE_HEIGHT/2 - getSurface().getHeight()/2 + yOffset;
    getSurface().draw(g, left, top);
  }
  
  public boolean isUnit() {
    return false;
  }
  
  public boolean isCorpse() {
    return false;
  }
  
  // Does the object block unit movement?
  public boolean isObstacle() {
    return true;
  }
  
  public boolean isInteractable() {
    return false;
  }
  
  public void updateDepth() {
    depth = (getX()+getY()) * Constants.TILE_HEIGHT/2 + getDepthOffset();
  }
  
  public int getDepthOffset() {
    return depthOffset;
  }
}