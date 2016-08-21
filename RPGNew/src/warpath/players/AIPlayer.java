package warpath.players;

import java.util.ArrayList;
import java.util.List;

import warpath.units.Unit;

/**
 * Represents an AI player.  Not necessarily hostile; we'll likely use one
 * for friendly NPCs as well.
 */

public class AIPlayer implements Player {
  private final List<Unit> units;
  private final List<Player> friendlyPlayers;
  private final List<Player> hostilePlayers;
  
  public AIPlayer() {
    units = new ArrayList<>();
    friendlyPlayers = new ArrayList<>();
    hostilePlayers = new ArrayList<>();
  }

  @Override
  public List<Unit> getUnits() {
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
