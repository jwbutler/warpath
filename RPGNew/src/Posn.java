
public class Posn {
  /* A two-int tuple, corresponding to an (X,Y) position.
   * Most methods are defined both with posns and with individual
   * X and Y arguments.  I'm not sure which is better but sometimes this feels
   * more convenient. */
  private int x, y;
  
  public Posn(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public boolean equals(Posn p) {
    return ((this.x==p.getX())&&(this.y==p.getY()));
  }
  
  public int getX() { return x; }
  public int getY() { return y; }
  public String toString() {
    return "<Posn("+x+","+y+")>";
  }
}
