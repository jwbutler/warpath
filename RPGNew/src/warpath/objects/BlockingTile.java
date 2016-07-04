package warpath.objects;
import jwbgl.*;
import warpath.core.RPG;

/** A tile that blocks unit movement, e.g. water. */
public class BlockingTile extends Tile {
  public BlockingTile(Posn posn, String texture) {
    super(posn, texture);
  }

  @Override
  public boolean isBlocked() {
    return true;
  }
}
