package warpath.players;
import java.util.ArrayList;
import java.util.Hashtable;

import warpath.units.Unit;

/* The human player.  There will only be one. */

public class HumanPlayer implements Player {
  private int playerNumber;
  private ArrayList<Unit> units;
  private ArrayList<Unit> selectedUnits;
  private ArrayList<Player> friendlyPlayers;
  private ArrayList<Player> hostilePlayers;
  
  public HumanPlayer(int playerNumber) {
    units = new ArrayList<Unit>();
    selectedUnits = new ArrayList<Unit>();
    friendlyPlayers = new ArrayList<Player>();
    hostilePlayers = new ArrayList<Player>();
  }
  
  public ArrayList<Unit> getSelectedUnits() {
    return selectedUnits;
  }
  
  public boolean isHuman() {
    return true;
  }

  @Override
  public void setHostile(Player p) {
    // TODO Auto-generated method stub
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
  public ArrayList<Unit> getUnits() {
    return units;
  }

  public int getPlayerNumber() {
    return playerNumber;
  }

}
