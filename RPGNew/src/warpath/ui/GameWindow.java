package warpath.ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

import warpath.core.RPG;
import warpath.ui.creator.CharacterCreator;

/**
 * The main game window.  Everything else gets rendered inside this;
 * we use a CardLayout to switch between views.
 * TODO How can we load components in a more sane way? Load-on-demand
 * TODO Clean up commented lines
 */ 
public class GameWindow extends JFrame {
  private static GameWindow instance;

  private static final long serialVersionUID = 1L;

  private static final int DEFAULT_WIDTH = 800, DEFAULT_HEIGHT = 600;
  private static final int HUD_PANEL_HEIGHT = 80;
  public static final String MENU_CARD = "Menu";
  public static final String GAME_CARD = "Game";
  public static final String CHAR_CREATOR_CARD = "Creator";
  public static final String MAP_EDITOR_CARD = "Editor";

  private final JPanel panelContainer;
  private final CardLayout cardLayout;

  private CharacterCreator characterCreator;
  private GamePanel gamePanel;
  private HUDPanel hudPanel;
  private MenuPanel menuPanel;
  private MapMaker mapEditor;

  /**
   * Instantiates the game window with the specified width and height.
   * TODO Can we avoid using the driver as a parameter?
   */
  public GameWindow() {
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setVisible(true);
    setResizable(false);
    panelContainer = new JPanel();
    cardLayout = new CardLayout();
    panelContainer.setLayout(cardLayout);
    add(panelContainer);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  /**
   * Sets up the card layout and the panel container.
   * This would be part of the constructor, but the sequence of initialization is very wonky.
   * Maybe we can delay some of these initializations?
   */
  public void initCardLayout(RPG game) {
    panelContainer.setPreferredSize(new Dimension(getWidth(), getHeight()));
    characterCreator = new CharacterCreator(this, getWidth(), getHeight());
    
    // Make the Menu panel
    menuPanel = new MenuPanel(this, game, getWidth(), getHeight());
    menuPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));

    // Make the Game panel
    gamePanel = new GamePanel(game, getWidth(), getHeight());
    gamePanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
    gamePanel.setLayout(new BorderLayout());

    // Make the HUD panel (it's a sub-component of the game panel)
    hudPanel = new HUDPanel(game, getWidth(), HUD_PANEL_HEIGHT);
    hudPanel.setPreferredSize(new Dimension(getWidth(), HUD_PANEL_HEIGHT));
    gamePanel.add(hudPanel, BorderLayout.SOUTH);

    // Make the map editor panel
    mapEditor = new MapMaker();
    mapEditor.setPreferredSize(new Dimension(getWidth(), getHeight()));
    mapEditor.setLayout(new BorderLayout());

    // Add to the main panel (panelContainer) and prepare for display.
    panelContainer.add(menuPanel, MENU_CARD);
    panelContainer.add(gamePanel, GAME_CARD);
    panelContainer.add(characterCreator, CHAR_CREATOR_CARD);
    panelContainer.add(mapEditor, MAP_EDITOR_CARD);
    pack();
  }

  /**
   * This will be used to set the current display from other classes.
   */ 
  public void setCardLayout(String panel) {
    cardLayout.show(panelContainer, panel);
    pack();
  }

  public HUDPanel getHudPanel() {
	  return hudPanel;
  }

  public GamePanel getGamePanel() {
	  return gamePanel;
  }

  public CharacterCreator getCharacterCreator() {
    return characterCreator;
  }

  /**
   * Attempt to convert this to a Singleton.
   */
  public static GameWindow getInstance() {
    if (instance == null) {
      instance = new GameWindow();
    }
    return instance;
  }
}
