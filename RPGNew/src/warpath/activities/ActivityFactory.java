package warpath.activities;

import warpath.units.Unit;

/**
 * Created by Jack on 8/20/2016.
 */
public class ActivityFactory {
    public static FancyActivity standing(Unit sourceUnit) {
      return new FancyActivity.Builder()
      .sourceUnit(sourceUnit)
      .length(4)
      .build();
  }
}
