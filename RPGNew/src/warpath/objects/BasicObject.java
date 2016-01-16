package warpath.objects;
import java.awt.Graphics;

import jwbgl.*;
import warpath.core.Constants;
import warpath.core.RPG;

/** This class contains basic methods to be used by all sorts of game objects,
 * units or otherwise.  We'll be subclassing it a lot. */

public abstract class BasicObject implements GameObject {
  protected final RPG game;
  private int xOffset;
  private int yOffset;
  private int depthOffset;
  
  private Posn posn;
  private int depth;
  private Rect rect;
  protected Surface surface;
  
  public BasicObject(RPG game, Posn posn) {
    this.game = game;
    this.posn = posn;
    //this.xOffset = 0;
    //this.yOffset = 0;
    //this.depthOffset = 0;
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
  
  @Override
  public int getX() {
    return posn.getX();
  }

  @Override
  public int getY() {
    return posn.getY();
  }
  
  @Override
  public Posn getPosn() {
    return posn;
  }
  
  @Override
  public void setPosn(Posn p) {
    posn = p;
  }
  
  @Override
  public int getDepth() {
    return depth;
  }

  @Override
  public int getXOffset() {
    return xOffset;
  }

  @Override
  public int getYOffset() {
    return yOffset;
  }
  
  @Override
  public void setXOffset(int xOffset) {
    this.xOffset = xOffset; 
  }
  
  @Override
  public void setYOffset(int yOffset) {
    this.yOffset = yOffset; 
  }
  
  @Override
  public int getDepthOffset() {
    return depthOffset;
  }
  
  @Override
  public void setDepthOffset(int depthOffset) {
    this.depthOffset = depthOffset;
  }
  
  /** Draws the object onto the game panel using the specified offsets.
   * @param g - the AWT graphics object used to render it
   */
  @Override
  public void draw(Graphics g) {
    // Offsets have not been figured out yet, need camera shit
    Posn pixel = game.gridToPixel(posn); // returns top left
    int left = pixel.getX() + Constants.TILE_WIDTH/2 - getSurface().getWidth()/2 + xOffset;
    int top = pixel.getY() + Constants.TILE_HEIGHT/2 - getSurface().getHeight()/2 + yOffset;
    getSurface().draw(g, left, top);
  }
  
  @Override
  public boolean isUnit() {
    return false;
  }
  
  @Override
  public boolean isCorpse() {
    return false;
  }
  
  // Does the object block unit movement?
  @Override
  public boolean isObstacle() {
    return true;
  }
  
  @Override
  public boolean isInteractable() {
    return false;
  }
  
  public void updateDepth() {
    depth = (getX()+getY()) * Constants.TILE_HEIGHT/2 + getDepthOffset();
  }
  
}