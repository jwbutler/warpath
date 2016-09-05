package warpath.core;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import jwbgl.Posn;

/**
 * @author jbutler
 * @since ???
 * Handles all keyboard and mouse events from the main game.  Most of these
 * correspond to orders for the player unit, so this class does contain a fair
 * amount of game logic.
 */
public class InputHandler implements KeyListener, KeyEventDispatcher,
MouseListener, MouseMotionListener  {
  private boolean ctrlIsDown, shiftIsDown;
  private Posn mousePosn;
  private static InputHandler instance;

  public InputHandler() {
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
    RPG game = RPG.getInstance();
    switch(e.getKeyCode()) {
      case KeyEvent.VK_CONTROL:
        ctrlIsDown = false;
        break;
      case KeyEvent.VK_SHIFT:
        shiftIsDown = false;
        break;
      case KeyEvent.VK_UP:
        game.moveCamera(0,-Constants.CAMERA_INCREMENT_Y);
        break;
      case KeyEvent.VK_DOWN:
        game.moveCamera(0,Constants.CAMERA_INCREMENT_Y);
        break;
      case KeyEvent.VK_LEFT:
        game.moveCamera(-Constants.CAMERA_INCREMENT_X, 0);
        break;
      case KeyEvent.VK_RIGHT:
        game.moveCamera(Constants.CAMERA_INCREMENT_X, 0);
        break;
      case KeyEvent.VK_SPACE:
        game.centerCamera();
        break;
      case KeyEvent.VK_W:
        if (ctrlIsDown) {
          game.killAllEnemies();
        }
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

  /**
   * Called when a mouse button is released.  Used to process player unit
   * actions such as attacking, special attacks, movement and other
   * interactions.
   * @param e - The event containing mouse information
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    RPG game = RPG.getInstance();
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
          game.doAttackOrder(new Posn(e.getX(), e.getY()));
        }
        break;
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // BasicUnit selection stuff.
  }
  
  @Override
  public void mouseMoved(MouseEvent e) {
    mousePosn = new Posn(e.getX(), e.getY());
  }
  
  public boolean ctrlIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK);
  }
  
  public boolean shiftIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK);
  }
  
  public boolean altIsDown(MouseEvent e) {
    return ((e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) == MouseEvent.ALT_DOWN_MASK);
  }
  
  /**
   * Returns true if the given mouse button is down.
   * @param e - The event containing mouse information
   * @param mouseButton - The given mouse button (using constants from MouseEvent)
   */ 
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
  
  public Posn getMousePosn() {
    if (mousePosn == null) {
      // ehhh
      return new Posn(
        (int)MouseInfo.getPointerInfo().getLocation().getX(),
        (int)MouseInfo.getPointerInfo().getLocation().getY()
      );
    }
    return mousePosn;
  }

  public boolean ctrlIsDown() {
    return ctrlIsDown;
  }

  public boolean shiftIsDown() {
    return shiftIsDown;
  }

  public static InputHandler getInstance() {
    if (instance == null) {
      instance = new InputHandler();
    }
    return instance;
  }
}
