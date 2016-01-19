package warpath.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import javax.swing.JPanel;

import jwbgl.*;
import warpath.core.RPG;
 
 /**
  * Represents the actual draw surface of the game.
  * TODO Learn how AWT/Swing graphics actually work...
  * */
public class GamePanel extends JPanel {
  private final RPG game;
  private KeyboardFocusManager focusManager;

  public GamePanel(RPG game, int width, int height) {
    super();
    setSize(width, height);
    this.game = game;
    //getContentPane().setBackground(Color.BLACK);
  }
  
  /**
   * To be called when the RPG class calls start().
   */
  public void init() {
    focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    focusManager.addKeyEventDispatcher(game.getInputHandler());
    addMouseListener(game.getInputHandler());
    addMouseMotionListener(game.getInputHandler());
    setVisible(true);
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
  }
  
  public void paint(Graphics g) {
    //System.out.println("paint");
    super.paint(g);
    //game.drawAll(g);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    game.drawAll(g);
  }
  
  public Rect getScreenRect() {
    return new Rect(0,0,getWidth(),getHeight());
  }
  
  public Rect getRect() {
    return new Rect(0,0,getWidth(),getHeight());  
  } 
}