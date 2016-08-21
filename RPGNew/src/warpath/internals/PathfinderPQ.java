package warpath.internals;
import java.util.Comparator;
import java.util.PriorityQueue;
import jwbgl.*;
/**
 * A Priority Queue that stores entries in the form
 * <weight, (x,y)>.  Used by the A* pathfinding algorithm.
 * What's a good initial capacity?
 */
public class PathfinderPQ extends PriorityQueue<PathfinderEntry> {
  private static final long serialVersionUID = 1L;
  private static final int INITIAL_CAPACITY = 128;
  
  public PathfinderPQ() {
    super(INITIAL_CAPACITY);
  }
  /**
   * Find and return (without removing) the entry matching the specified posn.
   */
  public PathfinderEntry getPosnEntry(Posn p) {
    for (PathfinderEntry e : this) {
      Posn q = e.getPosn();
      if (q.equals(p)) {
        return e;
      }
    }
    return null;
  }
}
