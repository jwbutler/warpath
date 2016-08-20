package warpath.internals;
import java.awt.Graphics;

import warpath.objects.GameObject;

/** This is my attempt to use CSE 2100 principles to draw stuff in a
 * semi-efficient way.  It's a binary search tree of GameObjects, ordered by
 * their depth.
 * TODO See if we can optimize some of these methods. */
public class DepthTree {
  private DepthTreeNode root;
  
  public DepthTree() {
    // Method of the century
    root = null;
  }
  
  /** Inserts an object into the tree, first constructing a DepthTreeNode
   * around it.
   * @param obj - The object to insert
   */
  public void add(GameObject obj) {
    add(new DepthTreeNode(obj));
  }
  
  /** Inserts a node into the tree.  Recursively calls
   * {@link #add(DepthTreeNode, DepthTreeNode)}.
   * @param n - The DepthTreeNode to insert
   */
  private void add(DepthTreeNode n) {
    if (root == null) {
      root = n;
    } else {
      add(n, root);
    }
  }
  
  /**
   * Inserts an object into the subtree rooted at current.
   * @param node - The DepthTreeNode containing the object to insert
   * @param current - The root of the current subtree
   */
  private void add(DepthTreeNode node, DepthTreeNode current) {
    if (node.getData().getDepth() <= current.getData().getDepth()) {
      if (current.getLeft() == null) {
        current.setLeft(node);
      } else {
        add(node, current.getLeft());
      }
    } else { // if (node.getData().getDepth() > current.getData().getDepth()) {
      if (current.getRight() == null) {
        current.setRight(node);
      } else {
        add(node, current.getRight());
      }
    }
  }
  
  /**
   * Removes the given GameObject from the tree.  Recursively calls
   * {@link #remove(GameObject, DepthTreeNode)}.
   * @param o - The object to remove
   * @return true if the operation succeeds, false otherwise
   */
  public boolean remove(GameObject o) {
    if (root == null) {
      System.out.printf("Remove failed: %s (1)\n", o);
      return false;
    } else if (root.getData() == o) {
      //System.out.println("rmv root");
      DepthTreeNode left = root.getLeft();
      DepthTreeNode right = root.getRight();
      root = null;
      if (left != null) add(left);
      if (right != null) add(right);
      return true;
    } else {
      return remove(o, root);
    }
  }
  
  /**
   * Removes an object from the subtree rooted at current.
   * @param o - The object to remove
   * @param current - The root of the current subtree
   * @return true if the operation succeeds, false otherwise
   */
  private boolean remove(GameObject o, DepthTreeNode current) {
    DepthTreeNode left = current.getLeft();
    DepthTreeNode right = current.getRight();
    if (o.getDepth() <= current.getData().getDepth()) {
      if (left == null) {
        System.out.printf("Remove failed: %s (2)\n", o);
        return false;
      } else if (left.getData() == o) {
        DepthTreeNode leftleft = left.getLeft();
        DepthTreeNode leftright = left.getRight();
        current.setLeft(null);
        if (leftleft != null) add(leftleft);
        if (leftright != null) add(leftright);
        return true;
      } else {
        return remove(o, left);
      }
    } else { // if (o.getDepth() > current.getData().getDepth()) {
      if (right == null) {
        System.out.printf("Remove failed: %s (3)\n", o);
        return false;
      } else if (right.getData() == o) {
        DepthTreeNode rightleft = right.getLeft();
        DepthTreeNode rightright = right.getRight();
        current.setRight(null);
        if (rightleft != null) add(rightleft);
        if (rightright != null) add(rightright);
        return true;
      } else {
        return remove(o, right);
      }
    }
  }
  
  /** Recursively calls the draw() method of each object in the tree, using
   * an in-order traversal.
   * @param g - the AWT Graphics object used to draw.
   */
  public void drawAll(Graphics g) {
    //System.out.println("Depth tree => drawAll() => head = " + head);
    if (root != null) {
      root.drawInOrder(g);
    }
  }
  
  /**
   * Returns the number of elements in the tree.
   * @return the number of elements in the tree
   */
  
  public int size() {
    return size(root);
  }
  
  
  /**
   * Returns the number of elements in the subtree rooted at root.
   * @param root - the root of the current subtree
   * @return the number of elements in the current subtree
   */
  private int size(DepthTreeNode root) {
    if (root == null) {
      return 0;
    } else {
      return size(root.getLeft()) + size(root.getRight()) + 1;
    }
  }

  public void clear() {
    root = null;
    
  }
}
