package warpath.players;
import java.util.ArrayList;

import warpath.units.BasicUnit;

/**
 * The human player.  There will only be one.
 */

public class HumanPlayer implements Player {
  private final int playerNumber;
  private final ArrayList<BasicUnit> units;
  private final ArrayList<BasicUnit> selectedUnits;
  private final ArrayList<Player> friendlyPlayers;
  private final ArrayList<Player> hostilePlayers;
  
  /**
   * Instantiates the player with the given player number index.
   */
  public HumanPlayer(int playerNumber) {
    this.playerNumber = playerNumber;
    units = new ArrayList<BasicUnit>();
    selectedUnits = new ArrayList<BasicUnit>();
    friendlyPlayers = new ArrayList<Player>();
    hostilePlayers = new ArrayList<Player>();
  }
  
  public ArrayList<BasicUnit> getSelectedUnits() {
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
  
  // ===== ACCESSOR METHODS =====
  public ArrayList<BasicUnit> getUnits() {
    return units;
  }

  public int getPlayerNumber() {
    return playerNumber;
  }

}
