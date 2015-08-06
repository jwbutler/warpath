import java.awt.Graphics;

/* ===== CHANGELOG =====
 * 5/25 - Moved to its own file.  Good practice and all.
 * ===================== */

public class DepthTreeNode {
  private DepthTreeNode left;
  private DepthTreeNode right;
  private GameObject data;
  
  public DepthTreeNode(GameObject data) {
    this.data = data;
    left = null;
    right = null;
  }
  public DepthTreeNode getLeft() {
    return left;
  }
  public DepthTreeNode getRight() {
    return right;
  }
  public GameObject getData() {
    return data;
  }
  public void setLeft(DepthTreeNode left) {
    this.left = left;
  }
  public void setRight(DepthTreeNode right) {
    this.right = right;
  }
  
  public void drawInOrder(Graphics g) {
    //System.out.println("DEPTH TREE: DRAWINORDER()");
    if (left != null) {
      left.drawInOrder(g);
    }
    //System.out.println("DEPTH TREE: DRAWING! (" + data + ")");
    //System.out.println(data + " " + data.getPosn());
    data.draw(g);
    if (right != null) {
      right.drawInOrder(g);
    }
  }
  
  public String toString() {
    return String.format("DepthTreeNode<%s>", getData());
  }
}
