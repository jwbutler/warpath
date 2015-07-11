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
 
 /* ===== CHANGELOG =====
  * 5/23 - Changed from JFrame to JPanel to fix flickering problems.
  * ===================== */

public class GamePanel extends JPanel implements KeyListener, KeyEventDispatcher,
MouseListener, MouseMotionListener {
  private RPG game;
  private KeyboardFocusManager focusManager;
  public boolean ctrlIsDown, shiftIsDown;
  private Posn mousePosn;

  public GamePanel(RPG game, int width, int height) {
    super();
    setSize(width, height);
    this.game = game;
    //getContentPane().setBackground(Color.BLACK);
    focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    focusManager.addKeyEventDispatcher(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    setVisible(true);
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    ctrlIsDown = shiftIsDown = false;
    mousePosn = null;
  }
  
  public void keyPressed(KeyEvent e) {
    int c = e.getKeyCode(); 
    if (c == KeyEvent.VK_CONTROL) {
      ctrlIsDown = true;
    } else if (c == KeyEvent.VK_SHIFT) {
      shiftIsDown = true;
    }
  }

  public void keyReleased(KeyEvent e) {
    // Pressing the arrow keys (up/down/left/right) moves the camera.
    switch(e.getKeyCode()) {
      case KeyEvent.VK_CONTROL:
        ctrlIsDown = false;
        break;
      case KeyEvent.VK_SHIFT:
        shiftIsDown = false;
        break;
      case KeyEvent.VK_UP:
        game.moveCamera(0,-RPG.CAMERA_INCREMENT_Y);
        break;
      case KeyEvent.VK_DOWN:
        game.moveCamera(0,RPG.CAMERA_INCREMENT_Y);
        break;
      case KeyEvent.VK_LEFT:
        game.moveCamera(-RPG.CAMERA_INCREMENT_X, 0);
        break;
      case KeyEvent.VK_RIGHT:
        game.moveCamera(RPG.CAMERA_INCREMENT_X, 0);
        break;
      case KeyEvent.VK_SPACE:
        game.centerCamera();
        break;
      case 49: // 1
        break;
      case 50: // 2
        break;
      case 51: // 3
        break;
      case 52: // 4
        break;
      case 53: // 5
        break;
    }
  }

  public void keyTyped(KeyEvent e) {
    // unused
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent e) {
    switch (e.getID()) {
      case KeyEvent.KEY_PRESSED:
        keyPressed(e);
        return true;
      case KeyEvent.KEY_RELEASED:
        keyReleased(e);
        return true;
      case KeyEvent.KEY_TYPED:
        keyTyped(e);
        return true;
      default:
        return false;
    }
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
  
  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    switch (e.getButton()) {
      // Left click: select units
      case MouseEvent.BUTTON1:
        if (ctrlIsDown) {
        } else {
          game.doLeftClick(new Posn(e.getX(), e.getY()));
        }
        break;
      // Right click: interact with stuff.
      case MouseEvent.BUTTON3:
        // modifier precedence?
        if (ctrlIsDown) {
          game.doBashOrder(new Posn(e.getX(), e.getY()));
        //} else if (shiftIsDown) {
        } else {
          game.doRightClick(new Posn(e.getX(), e.getY()));
        }
        break;
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // Unit selection stuff.
  }
  
  @Override
  public void mouseMoved(MouseEvent e) {
    mousePosn = new Posn(e.getX(), e.getY());
  }
  
  /*
  public boolean ctrlIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK);
  }*/
  
  public boolean shiftIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK);
  }
  
  public boolean altIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) == MouseEvent.ALT_DOWN_MASK);
  }
  
  public boolean mouseButtonIsDown(MouseEvent e, int mouseButton) {
    if (mouseButton == MouseEvent.BUTTON1) {
      if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
        return true;
      }
    } else if (mouseButton == MouseEvent.BUTTON2) {
      if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) == MouseEvent.BUTTON2_DOWN_MASK) {
        return true;
      }
    } else if (mouseButton == MouseEvent.BUTTON3) {
      if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
        return true;
      }
    }
    return false;
  }
  
  public Rect getScreenRect() {
    return new Rect(0,0,getWidth(),getHeight());
  }
  
  public Rect getRect() {
    return new Rect(0,0,getWidth(),getHeight());  
  }
  
  public Posn getMousePosn() {
    return mousePosn;
  }
}
