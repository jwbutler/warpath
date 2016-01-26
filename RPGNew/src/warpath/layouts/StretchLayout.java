package warpath.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;

/**
 * This is a custom container layout based on the BoxLayout, but hopefully
 * more intuitive.  It allows you to specify the size of components relative
 * to a baseline value of 1; so when you add a component, you call
 * add(Component, weight), and if for example weight = 2, it will be twice
 * as wide/tall as a component with weight=1.  Like BoxLayout, it supports
 * both horizontal and vertical orientations.
 * @see https://docs.oracle.com/javase/tutorial/uiswing/layout/custom.html
 */
public class StretchLayout implements LayoutManager2 {
  private static final int DEFAULT_GAP = 10;
  public static final int X_AXIS = 0;
  public static final int Y_AXIS = 1;
  private final ArrayList<Component> components;
  private final ArrayList<Double> weights;
  private final int axis;
  private int gap;

  public StretchLayout(int axis, int gap) {
    super();
    this.axis = axis;
    this.gap = gap;
    components = new ArrayList<Component>();
    weights = new ArrayList<Double>();
  }
  public StretchLayout(int axis) {
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
   * TODO rounding/remainders
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
    double totalRelativeWidth = 0;
    for (double w : weights) {
      totalRelativeWidth += w;
    }
    int parentWidth = parent.getWidth() - parent.getInsets().left - parent.getInsets().right;
    int parentHeight = parent.getHeight() - parent.getInsets().top - parent.getInsets().bottom;
    int left = parent.getInsets().left;
    int numGaps = components.size() - 1;
    for (int i=0; i<components.size(); i++) {
      Component c = components.get(i);
      int width = (int)(((double)parentWidth - gap*numGaps) * (weights.get(i) / totalRelativeWidth));
      int height = parentHeight;
      int top = parent.getInsets().top + (int)((parentHeight - height)*c.getAlignmentY());
      c.setPreferredSize(new Dimension(width, height));
      c.setBounds(left, top, width, height);
      left += width + gap;
    }
  }
  
  private void layoutVertical(Container parent) {
    double totalRelativeHeight = 0;
    for (double w : weights) {
      totalRelativeHeight += w;
    }
    int parentWidth = parent.getWidth() - parent.getInsets().left - parent.getInsets().right;
    int parentHeight = parent.getHeight() - parent.getInsets().top - parent.getInsets().bottom;
    int top = parent.getInsets().top;
    int numGaps = components.size() - 1;
    for (int i=0; i<components.size(); i++) {
      Component c = components.get(i);
      int height = (int)(((double)parentHeight - gap*numGaps) * (weights.get(i) / totalRelativeHeight));
      int width = parentWidth;
      int left = parent.getInsets().left + (int)((parentWidth - width)*c.getAlignmentX());
      c.setPreferredSize(new Dimension(width, height));
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
    int i = components.indexOf(c);
    components.remove(i);
    weights.remove(i);
  }

  @Override
  public void addLayoutComponent(Component c, Object o) {
    components.add(c);
    if (o != null) { 
      weights.add((double)o);
    } else {
      weights.add(1.0);
    }
  }

  @Override
  public float getLayoutAlignmentX(Container parent) {
    return parent.getAlignmentX();
  }

  @Override
  public float getLayoutAlignmentY(Container parent) {
    return parent.getAlignmentY();
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