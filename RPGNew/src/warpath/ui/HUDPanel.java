package warpath.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import jwbgl.*;
import warpath.core.RPG;
public class HUDPanel extends JPanel {
  private RPG game;
  private static final int MARGIN = 16;
  private HealthBar healthBar;
  private int healthBarX, healthBarY;
  private int healthBarWidth, healthBarHeight;
  private EnergyBar energyBar;
  private int energyBarX, energyBarY;
  private int energyBarWidth, energyBarHeight;
  
  public HUDPanel(RPG game, int width, int height) {
    super();
    this.game = game;
    setSize(width, height);
    //setBackground(RPG.TRANSPARENT_WHITE);
    setBackground(new Color(128, 128, 128,128));//Color.DARK_GRAY);
    //setBackground(Color.DARK_GRAY);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createRaisedBevelBorder(),
      BorderFactory.createEmptyBorder(5,5,5,5)));
    validate();
  }
  public void init() {
    removeAll();
    JPanel hpPanel = new JPanel();
    hpPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    hpPanel.setOpaque(false);
    JPanel epPanel = new JPanel();
    epPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    epPanel.setOpaque(false);
    add(hpPanel);
    add(epPanel);
    validate();
    healthBarWidth = hpPanel.getWidth();
    healthBarHeight = hpPanel.getHeight();
    healthBar = new HealthBar(game.getPlayerUnit(), healthBarWidth, healthBarHeight);
    SurfacePanel hpbPanel = new SurfacePanel(healthBar);
    hpPanel.add(hpbPanel);
    energyBarWidth = epPanel.getWidth();
    energyBarHeight = epPanel.getHeight();
    energyBar = new EnergyBar(game.getPlayerUnit(), energyBarWidth, energyBarHeight);
    SurfacePanel epbPanel = new SurfacePanel(energyBar);
    epPanel.add(epbPanel);
    validate();
  }
  /* public void paint(Graphics g) {
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
  }*/
 
}
