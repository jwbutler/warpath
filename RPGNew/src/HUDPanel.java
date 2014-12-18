import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class HUDPanel extends JPanel {
  private RPG game;
  private static final int MARGIN = 16;
  private HealthBar healthBar;
  private int healthBarX, healthBarY;
  private int healthBarWidth, healthBarHeight;
  
  public HUDPanel(RPG game, int width, int height) {
    super();
    this.game = game;
    setSize(width, height);
    //setBackground(RPG.TRANSPARENT_WHITE);
    setBackground(Color.DARK_GRAY);
    setLayout(null);
    
  }
  
  public void addBars() {

    healthBarX = MARGIN;
    healthBarY = getHeight()*2/5;
    healthBarWidth = getWidth() - MARGIN*2;
    healthBarHeight = getHeight()*3/5 - 3/2*MARGIN;
    healthBar = new HealthBar(game.getPlayerUnit(), healthBarWidth, healthBarHeight);
  }
  
  public void paint(Graphics g) {
    super.paint(g);
    if (healthBar != null) {
      healthBar.draw(g, new Posn(healthBarX, healthBarY));
    }
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (healthBar != null) {
      healthBar.draw(g, new Posn(healthBarX, healthBarY));
    }
  }
 
}
