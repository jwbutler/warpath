package warpath.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import warpath.ui.GameWindow;
import warpath.ui.creator.CharacterCreator;

  /**
   * This is the file where we actually create the game parameters: adding
   * players, units, etc. to the game.  We can define different ones for
   * various levels, modes, whatever.
   */

public class RPGDriver {
  public static void main(String[] args) {
    new RPGDriver();
  }
  
  public RPGDriver() {
    try {
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      JFrame.setDefaultLookAndFeelDecorated(true);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    GameWindow window = GameWindow.getInstance();
    RPG game = RPG.getInstance();
    window.initCardLayout(game);
  }
}