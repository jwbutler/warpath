import java.util.ArrayList;

/* Interface for players.  Can represent a human player or an AI; I'm planning
 * to use Player objects to control friendly NPCs, hostile units, etc. */ 

public interface Player {
  public ArrayList<Unit> getUnits();
  public boolean isHuman();
  public void setHostile(Player p);
  public boolean isHostile(Player p);
  void setFriendly(Player p);
  boolean isFriendly(Player p);
}
