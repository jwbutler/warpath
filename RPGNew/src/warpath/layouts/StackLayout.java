package warpath.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * This is a custom container layout based on the BoxLayout, but hopefully
 * more intuitive.  Components are set to their preferred sizes; no stretching
 * will occur.
 * @see https://docs.oracle.com/javase/tutorial/uiswing/layout/custom.html
 * @see StretchLayout
 */
public class StackLayout implements LayoutManager2 {
  private static final int DEFAULT_GAP = 10;
  public static final int X_AXIS = 0;
  public static final int Y_AXIS = 1;
  private final int axis;
  private int gap;

  public StackLayout(int axis, int gap) {
    super();
    this.axis = axis;
    this.gap = gap;
  }
  
  public StackLayout(int axis) {
    this(axis, DEFAULT_GAP);
  }
  
  /**
   * "Layout managers that do not associate strings with their components
   * generally do nothing in this method."
   */
  @Override
  public void addLayoutComponent(String s, Component c) {
  }

  /**
   * TODO rounding
   */
  @Override
  public void layoutContainer(Container parent) {
    if (axis == X_AXIS) {
      layoutHorizontal(parent);
    } else { // axis == Y_AXIS
      layoutVertical(parent);
    }
  }
  
  private void layoutHorizontal(Container parent) {
    int parentWidth = parent.getWidth() - parent.getInsets().left - parent.getInsets().right;
    int parentHeight = parent.getHeight() - parent.getInsets().top - parent.getInsets().bottom;
    int left = parent.getInsets().left;
    for (int i=0; i<parent.getComponents().length; i++) {
      Component c = parent.getComponents()[i];
      int width = (int)(c.getPreferredSize().getWidth());
      int height = (int)(c.getPreferredSize().getHeight());
      int top = parent.getInsets().top + (int)((parentHeight - height)*c.getAlignmentY());
      c.setBounds(left, top, width, height);
      left += width + gap;
    }
  }
  
  private void layoutVertical(Container parent) {
    int parentWidth = parent.getWidth() - parent.getInsets().left - parent.getInsets().right;
    int parentHeight = parent.getHeight() - parent.getInsets().top - parent.getInsets().bottom;
    int top = parent.getInsets().top;
    // Calculate the amount of extra space, so that we can assign
    // reasonable values to components that don't have a preferred size.
    // Extra space will be 
    int extraSpace = parentHeight - gap * (parent.getComponents().length - 1);
    int numUnsizedComponents = 0;
    for (int i=0; i<parent.getComponents().length; i++) {
      Component c = parent.getComponents()[i];
      if (c.getPreferredSize().height > 0) {
        extraSpace -= c.getPreferredSize().height;
      } else {
        numUnsizedComponents++;
      }
    }
    for (int i=0; i<parent.getComponents().length; i++) {
      Component c = parent.getComponents()[i];
      int width, height;
      if (c.getPreferredSize().getWidth() == 0) {
        width = parentWidth;
      } else {
        width = (int)(c.getPreferredSize().getWidth());
      }

      if (c.getPreferredSize().getHeight() == 0) {
        // What is a reasonable default value?
        height = (int)((double)extraSpace / numUnsizedComponents);
      } else {
        height = (int)(c.getPreferredSize().getHeight());
      }
      int left = parent.getInsets().left + (int)((parentWidth - width)*c.getAlignmentX());
      c.setBounds(left, top, width, height);
      top += height + gap;
    }
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    return new Dimension(0,0);
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    return new Dimension(parent.getWidth(), parent.getHeight());
  }

  @Override
  public void removeLayoutComponent(Component c) {
  }

  @Override
  public void addLayoutComponent(Component c, Object o) {
  }

  /** FIXME */
  @Override
  public float getLayoutAlignmentX(Container parent) {
    return 0.5f;
  }

  /** FIXME */
  @Override
  public float getLayoutAlignmentY(Container parent) {
    return 0.5f;
  }

  @Override
  public void invalidateLayout(Container parent) {
  }

  @Override
  public Dimension maximumLayoutSize(Container parent) {
    return new Dimension(
      parent.getWidth() - parent.getInsets().left - parent.getInsets().right,
      parent.getHeight() - parent.getInsets().top - parent.getInsets().bottom
    );
  }
}