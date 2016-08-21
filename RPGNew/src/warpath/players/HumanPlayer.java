package warpath.players;
import java.util.ArrayList;
import java.util.List;

import warpath.units.Unit;

/**
 * The human player.  There will only be one.
 */

public class HumanPlayer implements Player {
  private final int playerNumber;
  private final List<Unit> units;
  private final List<Unit> selectedUnits;
  private final List<Player> friendlyPlayers;
  private final List<Player> hostilePlayers;
  
  /**
   * Instantiates the player with the given player number index.
   */
  public HumanPlayer(int playerNumber) {
    this.playerNumber = playerNumber;
    units = new ArrayList<>();
    selectedUnits = new ArrayList<>();
    friendlyPlayers = new ArrayList<>();
    hostilePlayers = new ArrayList<>();
  }
  
  public List<Unit> getSelectedUnits() {
    return selectedUnits;
  }
  
  public boolean isHuman() {
    return true;
  }

  @Override
  public void setHostile(Player p) {
    if (!hostilePlayers.contains(p)) {
      hostilePlayers.add(p);
    }
    if (friendlyPlayers.contains(p)) {
      friendlyPlayers.remove(p);
    }
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

  public List<Unit> getUnits() {
    return units;
  }

  public int getPlayerNumber() {
    return playerNumber;
  }

}
