import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Comparator;

/* This is my attempt to use CSE 2100 principles to draw stuff in a semi-efficient way.
 * It's a binary tree of GameObjects, organized by their depth.
 * I'm pretty sure a lot of these methods can be implemented more efficiently.
 * Let's crack the 2100 book? */

public class DepthTree2 extends TreeMap<GameObject, GameObject> {
  public DepthTree2() {
    super(new DepthComparator());
  }
  
  public boolean add(GameObject obj) {
    int preSize = size();
    Collection<GameObject> preValues = this.values();
    GameObject rtn = put(obj, obj);
    int postSize = size();
    Collection<GameObject> postValues = this.values();
    System.out.println("add (" + obj + "):");
    for (GameObject o : preValues) {
      System.out.println(o);
      if (!postValues.contains(o)) {
        System.out.println("- " + o);
      }
    }
    
    for (GameObject o : postValues) {
      System.out.println(o);
      if (!preValues.contains(o)) {
        System.out.println("-- " + o);
      }
    }
    
    return (rtn == null);
  }
  
  public GameObject remove(GameObject o) {
    return super.remove(o);
  }
  
  public boolean contains(GameObject o) {
    return containsKey(o);
  }
  
  public void drawAll(Graphics g) {
    // should be in correct order
    for (GameObject o: this.values()) {
      o.draw(g);
    }
  }

  private static class DepthComparator implements Comparator<GameObject> {
    @Override
    public int compare(GameObject o1, GameObject o2) {
      // TODO Auto-generated method stub
      int d1 = o1.getDepth();
      int d2 = o2.getDepth();
      if (d1 < d2) {
        return -1;
      } else if (d1 == d2) {
        return 0;
      } else { // d1 > d2 {
        return 1;
      }
    }
  }
}
