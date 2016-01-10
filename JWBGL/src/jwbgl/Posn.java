package jwbgl;
public class Posn {
  /** A two-int tuple, corresponding to an (X,Y) position.
   * Most methods are defined both with posns and with individual
   * X and Y arguments.  I'm not sure which is better but sometimes this feels
   * more convenient. */
  public int x;
  public int y;
  
  public Posn(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public boolean equals(Posn p) {
    return ((this.x == p.x) && (this.y == p.y));
  }
  
  public int getX() { return x; }
  public int getY() { return y; }
  public String toString() {
    return "<Posn("+x+","+y+")>";
  }
  
  public Posn add(Posn p) {
    return new Posn(x + p.x, y + p.y);
  }
  
  public Posn clone() {
    return new Posn(x,y);
  }
}
