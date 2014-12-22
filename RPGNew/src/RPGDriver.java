import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

  /* This is the file where we actually create the game parameters: adding
   * players, units, etc. to the game.  We can define different ones for
   * various levels, modes, whatever. */

public class RPGDriver extends WindowAdapter implements ActionListener {
  private HashMap<Color, Color> swaps;
  private final int DEFAULT_WIDTH = 800, DEFAULT_HEIGHT = 600;
  private RPG game;
  private GameWindow window;
  private CharacterCreator cc;
  
  public static void main(String[] args) {
    RPGDriver me = new RPGDriver();
    me.doIt();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // continue button
    System.out.println(e.getActionCommand());
    if (e.getActionCommand().equals("Continue...")) {
      System.out.println("hello.");
      CharacterCreator cc = window.getCharacterCreator();
      swaps = cc.exportPaletteSwaps();
      window.setCardLayout("Game");
      startGame();
    }
  }
  public void windowClosed(WindowEvent e) { windowClosing(e); }
  
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
  
  public void doIt() {
    try {
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      JFrame.setDefaultLookAndFeelDecorated(true);
    } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    window = new GameWindow(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    game = new RPG(window);
    window.initCardLayout(game);
  }
  public void startGame() {

    // Add some player units.
    //HumanUnit u = new HumanUnit(me, "u", new Posn(3,4), me.getHumanPlayer());
    SwordGuy u = new SwordGuy(game, "u", new Posn(3,4), game.getHumanPlayer(), swaps);
    //SwordGirl u = new SwordGirl(me, "u", new Posn(3,4), me.getHumanPlayer());
    game.addUnit(u);
    
    // Make a hostile AI player
    game.addPlayer(2, new AIPlayer());
    game.getHumanPlayer().setHostile(game.getPlayer(2));
    game.getPlayer(2).setHostile(game.getPlayer(1));
    
    Level testLevel = new TestLevel(game);
    game.openLevel(testLevel);
    game.start();
  }
}
