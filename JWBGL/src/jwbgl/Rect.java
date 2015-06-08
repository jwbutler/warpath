package jwbgl;
import java.awt.Rectangle;


public class Rect {
  /* Represents a rectangle, used for a variety of purposes: collision,
   * selection, whatever.  Based on the pygame Rect class.
   * REALLY need to figure out off-by-one stuff
   * Todo: getRight(), getBottom() */
  private int left;
  private int top;
  private int width;
  private int height;

  public Rect(int left, int top, int width, int height) {
    super();
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
  }
  public Rect(Rectangle r) {
    this((int)r.getX(),
         (int)r.getY(),
         (int)r.getWidth(),
         (int)r.getHeight());
  }
  
  public int getLeft() {
    return left;
  }

  public int getTop() {
    return top;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getRight() {
    return left+width;
  }
  
  public int getBottom() {
    return top+height;
  }
  
  public boolean collidePoint(int x, int y) {
    if ((x >= getLeft()) && (x < getRight())) {
      if ((y >= getTop()) && (y < getBottom())) {
        return true;
      }
    }
    return false;
  }
  
  public boolean collidePoint(int[] point) {
    return collidePoint(point[0], point[1]);
  }
  
  public boolean collidePoint(Posn p) {
    return collidePoint(p.getX(), p.getY());
  }
  
  public boolean collideRect(Rect r) {
    if (r.getRight() < getLeft()) return false;
    if (r.getBottom() < getTop()) return false;
    if (r.getLeft() > getRight()) return false;
    if (r.getTop() > getBottom()) return false;
    return true;
  }
  
  public boolean contains(Rect r) {
    // Does it need to be strictly inside?
    // For now, no.
    // Test Pygame implementation
    if (r.getLeft() < getLeft()) return false;
    if (r.getRight() > getRight()) return false;
    if (r.getTop() < getTop()) return false;
    if (r.getBottom() > getBottom()) return false;
    return true;
  }
  
  public String toString() {
    return "Rect("+getLeft()+","+getTop()+","+getWidth()+","+getHeight()+")";
  }
  
  public Rect clone() {
    return new Rect(getLeft(), getTop(), getWidth(), getHeight());
  }
  
  public void move(int x, int y) {
    left += x;
    top += y;
  }
  
  public void setLeft(int x) {
    left = x;
  }
  
  public void setTop(int y) {
    top = y;
  }
  public void setLocation(int x, int y) {
    // TODO Auto-generated method stub
    setLeft(x);
    setTop(y);
  }
}
