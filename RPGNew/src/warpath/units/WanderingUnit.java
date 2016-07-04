package warpath.units;
import java.util.Random;

import jwbgl.*;
import warpath.core.RPG;
import warpath.players.Player;

/**
 * Just a "fun" little AI unit that picks a random (non-blocked) floor tile
 * and paths to it.
 */
public class WanderingUnit extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private Random RNG;
  public WanderingUnit(String name, Posn posn, Player player) {
    super(name, posn, player);
    RNG = new Random();
  }

  public void doEvents() {
    RPG game = RPG.getInstance();
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
        x = RNG.nextInt(game.getFloor().getWidth());
        y = RNG.nextInt(game.getFloor().getHeight());
        while (game.getFloor().getTile(x,y).isBlocked()) {
          x = RNG.nextInt(game.getFloor().getWidth());
          y = RNG.nextInt(game.getFloor().getHeight());
        }
        setNextTargetPosn(new Posn(x,y));
      }
    }
  }
}
