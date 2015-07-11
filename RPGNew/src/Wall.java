import java.awt.Color;
import jwbgl.*;
public class Wall extends BasicObject {
  public Wall(RPG game, Posn posn, String imgPath) {
    super(game, posn);
    surface = new Surface(imgPath).scale2x();
    surface.setColorkey(Color.WHITE);
    setyOffset(-54); // trial and error...
  }
}