package warpath.units;

import jwbgl.Posn;

/**
 * Created by Jack on 8/20/2016.
 */
public interface Unit {
  public Posn getPosn();
  public int getCurrentHP();
  public int getMaxHP();
}
