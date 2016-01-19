package warpath.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

import warpath.core.RPG;
import warpath.core.RPGDriver;

/**
 * The main game window.  Everything else gets rendered inside this;
 * we use a CardLayout to switch between views.
 * TODO Clean up commented lines
 */ 
public class GameWindow extends JFrame {

  private final static int HUD_PANEL_HEIGHT = 80;
  
  private final JPanel panelContainer;
  private final RPGDriver driver;

  private final CardLayout cardLayout;
  private CharacterCreator cc;
  private GamePanel gamePanel;
  private HUDPanel hudPanel;
  private MenuPanel menuPanel;
  
  /** Instantiates the game window with the specified width and height.
   * TODO Can we avoid using the driver as a parameter? */
  public GameWindow(RPGDriver driver, int width, int height) {
    this.driver = driver;
    setSize(width, height);
    setVisible(true);
    setResizable(false);
    addWindowListener(driver);
    panelContainer = new JPanel();
    cardLayout = new CardLayout();
    panelContainer.setLayout(cardLayout);
    add(panelContainer);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  // Sets up the Card Layout and the panelContainer.
  public void initCardLayout(RPG game) {
    panelContainer.setPreferredSize(new Dimension(getWidth(), getHeight()));
    // To be moved to the menuPanel when I'm less lazy. 

    cc = new CharacterCreator(driver, this, getWidth(), getHeight());
    
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
