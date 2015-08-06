import java.awt.Graphics;

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
  
  /* This is an example of an inorder traversal. */
  public void drawInOrder(Graphics g) {
    if (left != null) {
      left.drawInOrder(g);
    }
    data.draw(g);
    if (right != null) {
      right.drawInOrder(g);
    }
  }
  
  public String toString() {
    return String.format("<DepthTreeNode(%s)>", getData());
  }
}
