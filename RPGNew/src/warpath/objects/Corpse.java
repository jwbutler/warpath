package warpath.objects;

import java.awt.Color;
import jwbgl.Posn;
import jwbgl.Surface;
import warpath.core.RPG;

public class Corpse extends NonBlockingObject {
  public Corpse(Posn posn, String imgPath) {
    super(posn);
    surface = new Surface(imgPath).scale2x();
    surface.setColorkey(Color.WHITE);
    setOffsets(0, -32);
  }
  public boolean isCorpse() {
    return true;
  }

}
