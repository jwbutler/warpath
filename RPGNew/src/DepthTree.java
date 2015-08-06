import java.awt.Graphics;

/* This is my attempt to use CSE 2100 principles to draw stuff in a semi-efficient way.
 * It's a binary tree of GameObjects, organized by their depth.
 * I'm pretty sure a lot of these methods can be implemented more efficiently.
 * Let's crack the 2100 book? */
 
public class DepthTree {
  private DepthTreeNode root;
  
  public DepthTree() {
    // Method of the century
    root = null;
  }
  
  public void add(GameObject obj) {
    add(new DepthTreeNode(obj));
  }
  
  public void add(DepthTreeNode node) {
    if (root == null) {
      root = node;
    } else {
      add(node, root);
    }
  }
  
  public void add(DepthTreeNode node, DepthTreeNode current) {
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
  public boolean remove(GameObject o, DepthTreeNode current) {
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
        //System.out.println("rmv left");
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
        //System.out.println("rmv right");
        if (rightleft != null) add(rightleft);
        if (rightright != null) add(rightright);
        return true;
      } else {
        return remove(o, right);
      }
    }
  }
  
  /*public void add(DepthTreeNode node) {
    if (root == null) {
      root = node;
    } else {
      DepthTreeNode current = root;
      while (current != null) {
        if (current.getData().getDepth() > node.getData().getDepth()) {
          if (current.getLeft() == null) {
            current.setLeft(node);
            return;
          } else {
            current = current.getLeft();
          }
        } else if (current.getData().getDepth() <= node.getData().getDepth()) {
          if (current.getRight() == null) {
            current.setRight(node);
            return;
          } else {
            current = current.getRight();
          }
        }
      }
    }
  }
  
  public boolean remove(GameObject obj) {
    if (root.getData() == obj) {
      DepthTreeNode oldRoot = root;
      root = null;
      if (oldRoot.getLeft() != null) {
        add(oldRoot.getLeft());
      }
      if (oldRoot.getRight() != null) {
        add(oldRoot.getRight());
      }
      return true;
    } else {
      DepthTreeNode current = root;
      while (current != null) {
        if (obj.getDepth() < current.getData().getDepth()) {
          if (current.getLeft() != null) {
            if (current.getLeft().getData() == obj) {
              DepthTreeNode oldLeft = current.getLeft();
              current.setLeft(null);
              if (oldLeft.getLeft() != null) {
                add(oldLeft.getLeft());
              }
              if (oldLeft.getRight() != null) {
                add(oldLeft.getRight());
              }
              return true;
            } else {
              current = current.getLeft();
            }
          } else {
            current = current.getLeft();
          }
        } else { // if (obj.getDepth() >= current.getData().getDepth()) {
          if (current.getRight() != null) {
            if (current.getRight().getData() == obj) {
              DepthTreeNode oldRight = current.getRight();
              current.setRight(null);
              if (oldRight.getLeft() != null) {
                add(oldRight.getLeft());
              }
              if (oldRight.getRight() != null) {
                add(oldRight.getRight());
              }
              return true;
            } else {
              current = current.getRight();
            }
          } else {
            current = current.getRight();
          }
        }
      }
    }
    System.out.println("removefail " + obj);
    return false;
  } */
  
  public void drawAll(Graphics g) {
    //System.out.println("Depth tree => drawAll() => head = " + head);
    if (root != null) {
      root.drawInOrder(g);
    }
  }
  
  public int size(DepthTreeNode root) {
    if (root == null) {
      return 0;
    } else {
      return size(root.getLeft()) + size(root.getRight()) + 1;
    }
  }
  public int size() {
    return size(root);
  }
}
