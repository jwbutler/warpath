package warpath.internals;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import jwbgl.*;
/** A Priority Queue that stores entries in the form
 * <weight, (x,y)>.  Used by A* the pathfinding algorithm.
 * What's a good initial capacity? */
public class PathfinderPQ extends PriorityQueue<PathfinderEntry> {
  private static final int INITIAL_CAPACITY = 128;
  /* private static class PathfinderComparator implements Comparator<PathfinderEntry> {
    @Override
    public int compare(PathfinderEntry e1, PathfinderEntry e2) {
      int w1 = e1.getWeight();
      int w2 = e2.getWeight();
      if (w1 < w2) return -1;
      else if (w1 == w2) return 0;
      else if (w1 > w2) return 1;
      else return 42; // wat
    }
  }*/
  
  public PathfinderPQ() {
    super(INITIAL_CAPACITY, new Comparator<PathfinderEntry>() {
      public int compare(PathfinderEntry e1, PathfinderEntry e2) {
        return Double.compare(e1.getWeight(), e2.getWeight());
      }
    });
  }
  /** Find and return (without removing) the entry matching the specified posn.
   */
  public PathfinderEntry getPosnEntry(Posn p) {
    //Iterator<PathfinderEntry> i = this.iterator();
    //while (i.hasNext()) {
    //  PathfinderEntry e = this.iterator().next();
    for (PathfinderEntry e : this) {
      Posn q = e.getPosn();
      if (q.equals(p)) {
        return e;
      }
    }
    return null;
  }
}
