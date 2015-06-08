import java.awt.Color;
import java.util.HashMap;
import jwbgl.*;
/* Warrior-type player unit class.  Sword/shield.*/
public class SwordGuy extends HumanUnit {
  private int minDamage, maxDamage;
  private static String[] activities = {
    "walking", "standing", "attacking", "blocking_1", "blocking_2", "blocking_3",
    "bashing", "slashing_1", "slashing_2", "slashing_3"};

  public SwordGuy(RPG game, String name, Posn posn, Player player, HashMap<Color, Color> paletteSwaps) {
    /*public HumanUnit(RPG game, String name, String animationName,
        String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {*/
    super(game, name, activities, paletteSwaps, posn, player);
    currentHP = maxHP = 100;
    minDamage = 5;
    maxDamage = 10;
    addAccessory(new Sword(game, this, "sword"));
    addAccessory(new Shield(game, this, "Shield of Suck"));
  }
  public SwordGuy(RPG game, String name, Posn posn, Player player) {
    this(game, name, posn, player, new HashMap<Color, Color>());
  }

  public void doAttackHit(Unit u) {
    int dmg = game.getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeHit(this, dmg);
  }
  public void doBashHit(Unit u) {
    int d = 30; // ...
    int dx = u.getX() - getX();
    int dy = u.getY() - getY();
    u.move(dx, dy);
    u.setCurrentActivity("stunned_short");
    u.clearTargets();
    u.takeHit(this, d);
    //System.out.println(this + " hit unit " + u);
  }

}
