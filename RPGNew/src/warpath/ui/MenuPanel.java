package warpath.ui;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import warpath.core.RPG;

/** The panel for the menu used at the start of the game. */
public class MenuPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private final GameWindow window;
	private final RPG game;

	public MenuPanel(GameWindow gameWindow, RPG game, int width, int height) {
		super();
		window = gameWindow;
		this.game = game;
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
		//setBackground(Color.DARK_GRAY);
    setVisible(true);
		this.setLayout(new GridLayout(3, 1, 0, 25));
		this.setBorder(new EmptyBorder(300, 250, 50, 250));
		
		// Make and add buttons
	    JButton playButton = new JButton("Character Creator");
	    JButton quickPlayButton = new JButton("Quick Start");
      JButton mapEditorButton = new JButton("Map Editor");
	    JButton exitButton = new JButton("Exit");
	    this.add(playButton);
	    this.add(quickPlayButton);
	    this.add(exitButton);

	    playButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        window.setCardLayout(GameWindow.CHAR_CREATOR_CARD);
	      }
	    });
	    
      quickPlayButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          window.setCardLayout(GameWindow.GAME_CARD);
          game.start();
        }
      });

      mapEditorButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          window.setCardLayout(GameWindow.MAP_EDITOR_CARD);
        }
      });
	    
	    exitButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        System.exit(0);
	      }
	    });

	    
	}
}
