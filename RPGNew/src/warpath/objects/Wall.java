package warpath.objects;
import java.awt.Color;
import jwbgl.*;
import warpath.core.RPG;

public class Wall extends BasicObject {
  private final static int X_OFFSET = 0;
  private final static int Y_OFFSET = -54; // trial and error...
  private final static int DEPTH_OFFSET = 0;
  public Wall(RPG game, Posn posn, String imgPath) {
    super(game, posn);
    setXOffset(X_OFFSET);
    setYOffset(Y_OFFSET);
    setDepthOffset(DEPTH_OFFSET);
    surface = new Surface(imgPath).scale2x();
    surface.setColorkey(Color.WHITE);
  }
}