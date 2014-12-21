import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameWindow extends JFrame {

  private JPanel panelContainer = new JPanel();
  private RPGDriver driver;
  private CharacterCreator cc;

  private CardLayout cardLayout;
  private GamePanel gamePanel;
  private HUDPanel hudPanel;
  private JPanel menuPanel;
  private final int HUD_PANEL_HEIGHT = 80;
  
  public GameWindow(RPGDriver driver, int width, int height) {
    this.driver = driver;
    setSize(width, height);
    setVisible(true);
    setResizable(false);
    addWindowListener(driver);
    add(panelContainer);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    //The below call deals with setting up cardlayout for switching between JPanels
    //pack();
  }

  // Sets up the Card Layout and the panelContainer.
  public void initCardLayout(RPG game) {
    cardLayout = new CardLayout();
    panelContainer.setLayout(cardLayout);
    panelContainer.setPreferredSize(new Dimension(getWidth(), getHeight()));
    // To be moved to the menuPanel when I'm less lazy. 

    cc = new CharacterCreator(driver, getWidth(), getHeight());
    
    // Make the Menu Panel
    menuPanel = new JPanel();
    menuPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
    menuPanel.setLayout(null);
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
    
    // Make and add buttons
    JButton playButton = new JButton("Play");
    JButton exitButton = new JButton("Exit");
    panelContainer.add(menuPanel,"Menu");
    panelContainer.add(gamePanel,"Game");
    panelContainer.add(cc,"Creator");
    menuPanel.add(playButton);
    menuPanel.add(exitButton);
    
    // Position and Size
    int width = getWidth() * 1/5;
    int height = getHeight() * 1/10;
    int margin = 16;
    
    //playButton.setMargin(new Insets(50,50,50,50));
    playButton.setBounds((int)(getWidth()*.3),getHeight()*1/6,(int)(getWidth()*.4),height);
    playButton.setHorizontalAlignment(SwingConstants.CENTER);
    //playButton.addActionListener(this);
    
    //exitButton.setMargin(new Insets(5,5,5,5));
    exitButton.setBounds((int)(getWidth()*.3),getHeight()*3/6,(int)(getWidth()*.4),height);
    exitButton.setHorizontalAlignment(SwingConstants.CENTER);
    //exitButton.addActionListener(this);


    //cardLayout.show(panelContainer,"Menu");
    
    // These are the action performed methods for the buttons.
    playButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        cardLayout.show(panelContainer,"Creator");
      }
    });
    
    
    exitButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        System.exit(0);
      }
    });
    
    // Add to the main panel (panelContainer) and prepare for display.
  }
  
  // This will be used to set the current display from other classes. 
  public void setCardLayout(String panel) {
    if ( panel.equals("Game")) {
      cardLayout.show(panelContainer,"Game");
    }
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
