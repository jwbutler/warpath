package warpath.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jwbgl.*;
/* This class represents the game window.  It extends the JPanel class
 * and contains some painting methods.
 * It also handles input (mouse & keyboard), is this good design? */
import warpath.core.RPG;
 
 /* ===== CHANGELOG =====
  * 5/23 - Changed from JFrame to JPanel to fix flickering problems.
  * ===================== */

public class GamePanel extends JPanel {
  private RPG game;
  KeyboardFocusManager focusManager;

  public GamePanel(RPG game, int width, int height) {
    super();
    setSize(width, height);
    this.game = game;
    //getContentPane().setBackground(Color.BLACK);
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
