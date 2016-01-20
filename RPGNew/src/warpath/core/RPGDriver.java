package warpath.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import warpath.ui.CharacterCreator;
import warpath.ui.CharacterCreatorNew;
import warpath.ui.GameWindow;

  /**
   * This is the file where we actually create the game parameters: adding
   * players, units, etc. to the game.  We can define different ones for
   * various levels, modes, whatever.
   **/

public class RPGDriver extends WindowAdapter implements ActionListener {
  private final int DEFAULT_WIDTH = 800, DEFAULT_HEIGHT = 600;
  private final RPG game;
  private GameWindow window;
  
  public static void main(String[] args) {
    new RPGDriver();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // continue button
    if (e.getActionCommand().equals("Continue")) {
      CharacterCreatorNew cc = window.getCharacterCreator();
      window.setCardLayout("Game");
      game.start(cc.exportPaletteSwaps());
    }
  }
  
  public void windowClosed(WindowEvent e) { windowClosing(e); }
  
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
  
  public RPGDriver() {
    try {
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      JFrame.setDefaultLookAndFeelDecorated(true);
    } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    window = new GameWindow(this, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    game = new RPG(window);
    window.initCardLayout(game);
  }
}
