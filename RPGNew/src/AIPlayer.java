import java.util.ArrayList;

/* Represents an AI player.  Not necessarily hostile; we'll likekly use one
 * for friendly NPCs as well. */
 
/* ===== CHANGELOG =====
 * 5/26 - Created.
 * ===================== */

public class AIPlayer implements Player {
  private RPG game;
  private ArrayList<Unit> units;
  private ArrayList<Player> friendlyPlayers;
  private ArrayList<Player> hostilePlayers;
  
  public AIPlayer() {
    units = new ArrayList<Unit>();
    friendlyPlayers = new ArrayList<Player>();
    hostilePlayers = new ArrayList<Player>();
  }

  @Override
  public ArrayList<Unit> getUnits() {
    // TODO Auto-generated method stub
    return units;
  }

  @Override
  public boolean isHuman() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setHostile(Player p) {
    // TODO Auto-generated method stub
    if (!hostilePlayers.contains(p))
      hostilePlayers.add(p);
  }

  @Override
  public boolean isHostile(Player p) {
    // TODO Auto-generated method stub
    return hostilePlayers.contains(p);
  }

  @Override
  public void setFriendly(Player p) {
    // TODO Auto-generated method stub
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
