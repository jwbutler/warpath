import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/* The panel for the menu
 * ===== CHANGELOG =====
 * 12/22/14 	Created
 * =====================
 */

public class MenuPanel extends JPanel {

	private GameWindow window;
	private Floor floor;
	private RPG game;

	public MenuPanel(GameWindow theWindow, RPG theGame, int width, int height) {
		super();
		window = theWindow;
		game = theGame;
		setSize(width, height);
		setVisible(true);
		//setBackground(Color.CYAN);
		this.setLayout(null);
		
		// Make and add buttons
	    JButton playButton = new JButton("Play");
	    JButton exitButton = new JButton("Exit");
	    this.add(playButton);
	    this.add(exitButton);
	    
	    // Position and Size
	    int bHeight = getHeight() * 1/10;
	    

	    //playButton.setMargin(new Insets(50,50,50,50));
	    playButton.setBounds((int)(getWidth()*.3),getHeight()*1/6,(int)(getWidth()*.4),bHeight);
	    playButton.setHorizontalAlignment(SwingConstants.CENTER);
	    //playButton.addActionListener(this);
	    
	    //exitButton.setMargin(new Insets(5,5,5,5));
	    exitButton.setBounds((int)(getWidth()*.3),getHeight()*3/6,(int)(getWidth()*.4),bHeight);
	    exitButton.setHorizontalAlignment(SwingConstants.CENTER);
	    //exitButton.addActionListener(this);
		
	    
	    // These are the action performed methods for the buttons.
	    playButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
	        window.setCardLayout("Creator");
	      }
	    });
	    
	    
	    exitButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
	        System.exit(0);
	      }
	    });
	    
	}
	
	public void paint(Graphics g) {
		//System.out.println("paint");
		super.paint(g);
		//game.drawAll(g);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		game.getFloor().draw(g);
	}

}
