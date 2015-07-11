import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import jwbgl.*;
public class SurfacePanel extends JPanel {
  private Surface surface;
  private static final int DEFAULT_PADDING = 3;
  private int hpad, vpad;
  public SurfacePanel(Surface surface, int hpad, int vpad) {
    this.surface = surface;
    this.hpad = hpad;
    this.vpad = vpad;
    this.setPreferredSize(new Dimension(surface.getWidth()+2*hpad, surface.getHeight()+2*vpad));
    //this.setPreferredSize(getSize());
    System.out.printf("%s - %s, %s\n", this.getSize(), surface.getWidth(), surface.getHeight());
  }
  public SurfacePanel(Surface surface) {
    this(surface, DEFAULT_PADDING, DEFAULT_PADDING);
  }
  
  public void paint(Graphics g) {
    super.paint(g);
    surface.applyPaletteSwaps();
    surface.draw(g, hpad,  vpad);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    surface.applyPaletteSwaps();
    surface.draw(g, hpad,  vpad);
  }
  public void setSurface(Surface s) {
    surface = s;
  }
  
  
  
}
