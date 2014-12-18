import java.awt.Color;

public class Wall extends BasicObject {
  private Surface surface;
  public Wall(RPG game, Posn posn, String imgPath) {
    super(game, posn);
    surface = new Surface(imgPath).scale2x();
    surface.setColorkey(Color.WHITE);
    setyOffset(-56); // trial and error...
  }
  
  @Override
  public Surface getSurface() {
    // TODO Auto-generated method stub
    return surface;
  }
}