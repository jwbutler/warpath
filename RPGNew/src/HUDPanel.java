import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import jwbgl.*;
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
    setBackground(RPG.TRANSPARENT_WHITE);
    setBackground(new Color(128, 128, 128,128));//Color.DARK_GRAY);
    //setBackground(Color.DARK_GRAY);
    setLayout(null);
    setBorder(BorderFactory.createRaisedBevelBorder());
  }
  
  public void addBars() {

    healthBarX = MARGIN;
    healthBarY = getHeight()*2/5;
    healthBarWidth = getWidth()*2/3 - MARGIN*2;
    healthBarHeight = getHeight()*3/5 - 3/2*MARGIN;
    healthBar = new HealthBar(game.getPlayerUnit(), healthBarWidth, healthBarHeight);
    for (int i = 0; i < 5; i++) {
      JButton jb = new JButton();
      jb.setMargin(new Insets(2,2,2,2));
      jb.setText(Integer.toString(i));
      int width = (getWidth()*1/3 - 5*MARGIN)/5;
      int left = getWidth()*2/3 + (width+MARGIN)*i;
      jb.setBounds(left, healthBarY, width, healthBarHeight);
      add(jb);
    }
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
