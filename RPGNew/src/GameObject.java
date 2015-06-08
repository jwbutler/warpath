import java.awt.Graphics;

import jwbgl.*;

/* Basically everything that goes on a tile implements this.
 * Even tiles themselves! Unclear whether we need this in addition to the 
 * BasicObject class that gets extended by stuff. */ 

public interface GameObject {
  public int getX();
  public int getY();
  public Posn getPosn();
  public void setPosn(Posn p);
  public int getDepth();
  public Surface getSurface();
  public Rect getRect();
  public void draw(Graphics g);
  public void setxOffset(int xOffset);
  public void setyOffset(int yOffset);
  public int getxOffset();
  public int getyOffset();
  public boolean isUnit();
  public boolean isObstacle();
  public boolean isInteractable();
}
