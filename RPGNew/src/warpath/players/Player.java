package warpath.players;
import java.util.ArrayList;

import warpath.units.Unit;

/**
 * Interface for players.  Can represent a human player or an AI; I'm planning
 * to use Player objects to control groups of friendly NPCs, hostile units,
 * etc.
 * TODO Revamp the alignment system, use an enum (FRIENDLY, HOSTILE, NEUTRAL)
 * or maybe alliances
 */ 

public interface Player {
  public ArrayList<Unit> getUnits();
  public boolean isHuman();
  /** Indicates that this player is hostile toward p.
   * @param p - another player */
  public void setHostile(Player p);
  /** Indicates that this player is friendly toward p.
   * @param p - another player */
  public boolean isHostile(Player p);
  /** Indicates that this player is friendly toward p.
   * @param p - another player */
  void setFriendly(Player p);
  /** Indicates that this player is friendly toward p.
   * @param p - another player */
  boolean isFriendly(Player p);
}
