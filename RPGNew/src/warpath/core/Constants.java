package warpath.core;

import java.awt.Color;

public class Constants {

  public static final int FPS = 20; // should be 20
  public static final int TILE_WIDTH = 96;
  public static final int TILE_HEIGHT = 48;
  public static final int CAMERA_INCREMENT_X = 48;
  public static final int CAMERA_INCREMENT_Y = 24;
  public static final int HUD_PANEL_HEIGHT = 100;
  public static final int MENU_PADDING = 10;
  public static final String[] DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
  public static final Color TRANSPARENT_WHITE = new Color(0x00FFFFFF, true);
  
  public static final String CHARACTER_SAVE_FOLDER = "characters";
  public static final String CHARACTER_SAVE_FORMAT = "sav";
  public static final String IMAGE_FOLDER = "png";
  public static final String IMAGE_FORMAT = "png";
}
