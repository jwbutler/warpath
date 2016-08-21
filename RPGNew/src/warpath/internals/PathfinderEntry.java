package warpath.internals;
import jwbgl.*;

/** An entry used in the A* pathfinding algorithm.  Just a key-value pair
 * containing a Posn and its pathfinding weight.
 * @see PathfinderPQ
 */
public class PathfinderEntry implements Comparable<PathfinderEntry> {
  private final int weight;
  private final Posn posn;
  
  public PathfinderEntry(int weight, Posn posn) {
    this.weight = weight;
    this.posn = posn;
  }
  public int getWeight() {
    return weight;
  }
  public Posn getPosn() {
    return posn;
  }

  @Override
  public int compareTo(PathfinderEntry other) {
    return Integer.compare(getWeight(), other.getWeight());
  }
}
