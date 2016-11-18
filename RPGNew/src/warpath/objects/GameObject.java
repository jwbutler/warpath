package warpath.objects;
import java.awt.Graphics;

import jwbgl.*;

/** Basically everything that goes on a tile implements this.
 * Even tiles themselves! Unclear whether we need this in addition to the 
 * BasicObject class that gets extended by stuff. */ 

public interface GameObject {
  int getX();
  int getY();
  Posn getPosn();
  void setPosn(Posn p);
  int getDepth();
  Surface getSurface();
  Rect getRect();
  void draw(Graphics g);
  int getXOffset();
  int getYOffset();

  void setOffsets(int xOffset, int yOffset);

  int getDepthOffset();
  void setXOffset(int xOffset);
  void setYOffset(int yOffset);
  void setDepthOffset(int depthOffset);
  boolean isUnit();
  boolean isObstacle();
  boolean isCorpse();
  boolean isInteractable();
}
