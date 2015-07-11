import java.util.Random;
import jwbgl.*;
/* Just a "fun" little AI unit that picks a random (non-blocked) floor tile
 * and paths to it. */

public class WanderingUnit extends HumanUnit {
  private Random RNG;
  public WanderingUnit(RPG game, String name, Posn posn, Player player) {
    super(game, name, posn, player);
    RNG = new Random();
  }

  public void doEvents() {
    
    if (getCurrentActivity().equals("walking")) {
      if (getCurrentAnimation().getIndex() <= 2) {
        checkNextTile();
      }
      
      if (getCurrentAnimation().getIndex() == 2) {
        move(dx, dy);
        path.removeFirst();
      }
    } else if (getCurrentActivity().equals("standing")) {
      if (RNG.nextInt(30) == 0) {
        int x,y;
        x = RNG.nextInt(game.getFloor().width);
        y = RNG.nextInt(game.getFloor().height);
        while (game.getFloor().getTile(x,y).isBlocked()) {
          x = RNG.nextInt(game.getFloor().width);
          y = RNG.nextInt(game.getFloor().height);
        }
        setNextTargetPosn(new Posn(x,y));
      }
    }
  }
}
