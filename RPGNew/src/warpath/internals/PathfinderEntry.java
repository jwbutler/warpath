package warpath.internals;
import jwbgl.*;

/** An entry used in the A* pathfinding algorithm.  Just a key-value pair
 * containing a Posn and its pathfinding weight.
 * @see PathfinderPQ
 */
public class PathfinderEntry {
  private int weight;
  private Posn posn;
  
  public PathfinderEntry(int weight, Posn posn) {
    this.weight = weight;
    this.posn = posn;
  }
  public int getWeight() {
    return weight;
  }
  public void setWeight(int weight) {
    this.weight = weight;
  }
  public Posn getPosn() {
    return posn;
  }
  public void setPosn(Posn posn) {
    this.posn = posn;
  }
}
