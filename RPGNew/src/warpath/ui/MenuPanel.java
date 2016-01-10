package warpath.ui;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import warpath.core.RPG;
import warpath.objects.Floor;

/** The panel for the menu used at the start of the game. */
public class MenuPanel extends JPanel {

	private GameWindow window;
	private RPG game;

	public MenuPanel(GameWindow theWindow, RPG theGame, int width, int height) {
		super();
		window = theWindow;
		game = theGame;
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
		//setBackground(Color.DARK_GRAY);
    setVisible(true);
		this.setLayout(new GridLayout(3,1,0,25));
		this.setBorder(new EmptyBorder(300,250,50,250));
		
		// Make and add buttons
	    JButton playButton = new JButton("Character Creator");
	    JButton quickPlayButton = new JButton("Quick Start");
	    JButton exitButton = new JButton("Exit");
	    this.add(playButton);
	    this.add(quickPlayButton);
	    this.add(exitButton);
		
	    
	    // These are the action performed methods for the buttons.
	    playButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
	        window.setCardLayout("Creator");
	      }
	    });
	    
      quickPlayButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          window.setCardLayout("Game");
          game.start();
        }
      });
	    
	    exitButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
	        System.exit(0);
	      }
	    });
	    
	}
}
