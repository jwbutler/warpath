import java.util.ArrayList;

public class Sword extends Accessory {
  ArrayList<String> behindFrameNames;

  public Sword(RPG game, Unit unit, String animationName) {
    super(game, unit, animationName, "mainhand");
    yOffset = -10;
    xOffset = 0; // just guessing.
    
  }

}
