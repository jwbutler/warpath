package warpath.units;
import java.util.Random;

import jwbgl.*;
import warpath.activities.Activity;
import warpath.core.RPG;
import warpath.players.Player;

/**
 * Just a "fun" little AI unit that picks a random (non-blocked) floor tile
 * and paths to it.
 */
public class WanderingUnit extends HumanUnit {
  private static final long serialVersionUID = 1L;
  public WanderingUnit(String name, Posn posn, Player player) {
    super(name, posn, player);
  }

  public void doEvents() {
    RPG game = RPG.getInstance();
    if (getCurrentActivity().equals(Activity.WALKING)) {
      if (getCurrentAnimation().getIndex() <= 2) {
        checkNextTile();
      }
      
      if (getCurrentAnimation().getIndex() == 2) {
        move(dx, dy);
        path.remove(0);
      }
    } else if (getCurrentActivity().equals(Activity.STANDING)) {
      Random RNG = RPG.getInstance().getRNG();
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
