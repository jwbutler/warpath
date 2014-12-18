import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/* A modified Priority Queue that stores entries in the form
 * <weight, (x,y), parent>.  Used by the pathfinding algorithm.
 * Includes a custom comparator for handling the entries - it's just a simple
 * comparison of the weights.
 * What's a good initial capacity? */
public class PathfinderPQ extends PriorityQueue<PathfinderEntry> {
  private static final int INITIAL_CAPACITY = 128;
  
  private static class PathfinderComparator implements Comparator<PathfinderEntry> {
    @Override
    public int compare(PathfinderEntry e1, PathfinderEntry e2) {
      int w1 = e1.getWeight();
      int w2 = e2.getWeight();
      if (w1 < w2) return -1;
      else if (w1 == w2) return 0;
      else if (w1 > w2) return 1;
      else return 42; // wat
    }

  }
  public PathfinderPQ() {
    super(INITIAL_CAPACITY, new PathfinderComparator());
  }
  // Find and return (without removing) the entry matching the specified posn.
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
