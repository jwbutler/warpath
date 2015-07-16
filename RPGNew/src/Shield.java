import java.util.ArrayList;

public class Shield extends Accessory {
  //ArrayList<String> behindFrameNames;
  public Shield(RPG game, Unit unit, String animationName) {
    super(game, unit, "shield2", "offhand");
    yOffset = -10;
    xOffset = 0; // just guessing.
  }

}