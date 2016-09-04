package warpath.activities;

import warpath.units.Unit;

/**
 * @author jbutler
 * @since August 2016
 */
public class NullActivity implements Activity {
  @Override
  public int getLength() {
    return 0;
  }

  @Override
  public void doUpkeep(Unit u) {
  }

  @Override
  public void doEvents(Unit u) {
  }
}
