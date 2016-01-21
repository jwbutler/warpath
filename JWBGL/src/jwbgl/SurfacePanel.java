package jwbgl;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A JPanel that contains a Surface object.
 * Does this need to exist?
 */
public class SurfacePanel extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Surface surface;
  private static final int DEFAULT_PADDING = 3;
  private final int hpad, vpad;
  public SurfacePanel(Surface surface, int hpad, int vpad) {
    this.surface = surface;
    this.hpad = hpad;
    this.vpad = vpad;
    this.setLayout(null);
    this.setPreferredSize(new Dimension(surface.getWidth()+2*hpad, surface.getHeight()+2*vpad));
    //this.setSize(new Dimension(surface.getWidth()+2*hpad, surface.getHeight()+2*vpad));
    this.setMaximumSize(new Dimension(surface.getWidth()+2*hpad, surface.getHeight()+2*vpad));
    
    // doesn't respect vpad
    this.setBorder(BorderFactory.createLineBorder(Color.BLACK, hpad));
  }
  public SurfacePanel(Surface surface) {
    this(surface, DEFAULT_PADDING, DEFAULT_PADDING);
  }
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    surface.applyPaletteSwaps();
    surface.draw(g, hpad,  vpad);
  }
  
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    surface.applyPaletteSwaps();
    surface.draw(g, hpad,  vpad);
  }
  
  public void setSurface(Surface s) {
    surface = s;
  }
  
}
