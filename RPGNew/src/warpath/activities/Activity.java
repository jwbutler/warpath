package warpath.activities;

import warpath.units.Unit;

/**
 * @author jbutler
 * @since August 2016
 */
public interface Activity {
  int getLength();

  /**
   * Called at the beginning of the turn.
   * Includes things like resource cost payment for continuous activities
   * (e.g. SLASHING_2, BLOCKING_2).  Ideally it will include any checks
   * for conditions that may cancel the activity (e.g. blocked path,
   * insufficient resources).
   */
  void doUpkeep(Unit u);

  /**
   * Core activity handler code.
   * @param u
   */
  void doEvents(Unit u);
}
