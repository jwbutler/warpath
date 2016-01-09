package warpath.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

import warpath.core.RPG;
import warpath.core.RPGDriver;

public class GameWindow extends JFrame {

  private JPanel panelContainer = new JPanel();
  private RPGDriver driver;
  private CharacterCreator cc;

  private CardLayout cardLayout;
  private GamePanel gamePanel;
  private HUDPanel hudPanel;
  private MenuPanel menuPanel;
  private final int HUD_PANEL_HEIGHT = 80;
  
  public GameWindow(RPGDriver driver, int width, int height) {
    this.driver = driver;
    setSize(width, height);
    setVisible(true);
    setResizable(false);
    addWindowListener(driver);
    add(panelContainer);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  // Sets up the Card Layout and the panelContainer.
  public void initCardLayout(RPG game) {
    cardLayout = new CardLayout();
    panelContainer.setLayout(cardLayout);
    panelContainer.setPreferredSize(new Dimension(getWidth(), getHeight()));
    // To be moved to the menuPanel when I'm less lazy. 

    cc = new CharacterCreator(driver, getWidth(), getHeight());
    
    // Make the Menu Panel
    menuPanel = new MenuPanel(this,game,getWidth(),getHeight());
    menuPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
    //menuPanel.setLayout(null);
    //menuPanel.setLayout(new BorderLayout());

    // Make the Game Panel
    gamePanel = new GamePanel(game, getWidth(), getHeight());
    gamePanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
    gamePanel.setLayout(new BorderLayout());

    hudPanel = new HUDPanel(game, getWidth(), HUD_PANEL_HEIGHT);
    hudPanel.setPreferredSize(new Dimension(getWidth(), HUD_PANEL_HEIGHT));
    //hudPanel.setBounds(0, getHeight()-HUD_PANEL_HEIGHT, getWidth(), HUD_PANEL_HEIGHT);
    /*gamePanel.setAlignmentX(0.0f);
    gamePanel.setAlignmentY(0.0f);*/
    gamePanel.add(hudPanel, BorderLayout.SOUTH);

    // Add to the main panel (panelContainer) and prepare for display.
    panelContainer.add(menuPanel,"Menu");
    panelContainer.add(gamePanel,"Game");
    panelContainer.add(cc,"Creator");
    pack();

  }

  // This will be used to set the current display from other classes. 
  public void setCardLayout(String panel) {

    cardLayout.show(panelContainer, panel);
	  /*if ( panel.equals("Game")) {
		  cardLayout.show(panelContainer,"Game");
	  }
	  if ( panel.equals("Creator")) {
		  cardLayout.show(panelContainer,"Creator");
	  }*/
  }

  public HUDPanel getHudPanel() {
	  return hudPanel;
  }

  public GamePanel getGamePanel() {
	  return gamePanel;
  }
  

  public CharacterCreator getCharacterCreator() {
    return cc;
  }
}
