package warpath.activities;

import warpath.units.Unit;

/**
 * Created by Jack on 8/20/2016.
 */
public class ActivityFactory {
    public static Activity standing(Unit sourceUnit) {
      return new Activity.Builder()
      .sourceUnit(sourceUnit)
      .length(4)
      .build();
  }
}
