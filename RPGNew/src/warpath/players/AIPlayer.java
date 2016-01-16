package warpath.players;

import java.util.ArrayList;
import warpath.units.Unit;

/**
 * Represents an AI player.  Not necessarily hostile; we'll likely use one
 * for friendly NPCs as well.
 */

public class AIPlayer implements Player {
  private final ArrayList<Unit> units;
  private final ArrayList<Player> friendlyPlayers;
  private final ArrayList<Player> hostilePlayers;
  
  public AIPlayer() {
    units = new ArrayList<Unit>();
    friendlyPlayers = new ArrayList<Player>();
    hostilePlayers = new ArrayList<Player>();
  }

  @Override
  public ArrayList<Unit> getUnits() {
    return units;
  }

  @Override
  public boolean isHuman() {
    return false;
  }

  @Override
  public void setHostile(Player p) {
    if (!hostilePlayers.contains(p))
      hostilePlayers.add(p);
  }

  @Override
  public boolean isHostile(Player p) {
    return hostilePlayers.contains(p);
  }

  @Override
  public void setFriendly(Player p) {
    if (!friendlyPlayers.contains(p)) {
      friendlyPlayers.add(p);
    }
    if (hostilePlayers.contains(p)) {
      hostilePlayers.remove(p);
    }
  }
  
  @Override
  public boolean isFriendly(Player p) {
    return friendlyPlayers.contains(p);
  }

}
