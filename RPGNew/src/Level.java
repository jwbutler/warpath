import java.util.ArrayList;


public class Level {
  protected Floor floor;
  protected ArrayList<Unit> units;
  protected ArrayList<GameObject> objects;
  public Level(RPG game, int width, int height) {
    floor = new Floor(game, width, height);
    units = new ArrayList<Unit>();
    objects = new ArrayList<GameObject>();
  }
  public ArrayList<Unit> getUnits() { return units; }
  public ArrayList<GameObject> getObjects() { return objects; }
  public Floor getFloor() { return floor; }
}
