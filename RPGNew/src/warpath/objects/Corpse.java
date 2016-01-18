package warpath.objects;

import java.awt.Color;
import jwbgl.Posn;
import jwbgl.Surface;
import warpath.core.RPG;

public class Corpse extends NonBlockingObject {
  private final static int Y_OFFSET = -32;
  public Corpse(RPG game, Posn posn, String imgPath) {
    super(game, posn);
    surface = new Surface(imgPath).scale2x();
    surface.setColorkey(Color.WHITE);
    setYOffset(Y_OFFSET);
  }
  public boolean isCorpse() {
    return true;
  }

}
