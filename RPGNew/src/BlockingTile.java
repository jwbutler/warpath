
public class BlockingTile extends Tile {
  // We'll use this for river tiles etc.
  public BlockingTile(RPG game, Posn posn, String texture) {
    super(game, posn, texture);
  }

  public boolean isBlocked() {
    return true;
  }
}
