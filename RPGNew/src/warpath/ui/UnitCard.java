package warpath.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import warpath.core.RPG;
import warpath.units.Unit;

public class UnitCard extends JButton implements ActionListener {
  private HealthBar healthBar;
  private static final int MARGIN = 10;
  private static final int HEALTH_BAR_HEIGHT = 20;
  private static final Color SELECTED_COLOR = new Color(192,192,224,255);
  private static final Color UNSELECTED_COLOR = new Color(160,160,160,255);
  private Unit unit;
  private RPG game;
  public UnitCard(RPG game, Unit unit, int width, int height) {
    super();
    this.game = game;
    this.unit = unit;
    setSize(width,height);
    healthBar = new HealthBar(unit, width-2*MARGIN, HEALTH_BAR_HEIGHT);
    addActionListener(this);
  }
  @Override
  public void actionPerformed(ActionEvent arg0) {
    //System.out.println("hi.");    
  }
  
  @Override
  public void paint(Graphics g) {
    if (game.getHumanPlayer().getSelectedUnits().contains(unit)) {
      this.setBackground(SELECTED_COLOR);
    } else {
      this.setBackground(UNSELECTED_COLOR);
    }
    super.paint(g);
    int cardY = getHeight() - MARGIN - healthBar.getHeight();
    healthBar.draw(g, MARGIN, cardY);
  }
}
