package warpath.ui;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import jwbgl.*;
import warpath.core.RPG;
import warpath.ui.components.EnergyBar;
import warpath.ui.components.HealthBar;

/**
 * Represents the player HUD. Contains the health and energy bars.
 */
public class HUDPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private static final int MARGIN = 16;
  private final RPG game;
  private HealthBar healthBar;
  private int healthBarWidth, healthBarHeight;
  private EnergyBar energyBar;
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
  
  /**
   * I think there's some weirdness where this gets called separately from the
   * constructor because the player unit hadn't existed yet.
   */ 
  public void init() {
    removeAll(); // Why?
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
 
}
