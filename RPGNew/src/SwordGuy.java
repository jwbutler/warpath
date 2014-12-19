import java.awt.Color;
import java.util.HashMap;
/* Warrior-type player unit class.  Sword/shield.*/
public class SwordGuy extends HumanUnit {
  private int minDamage, maxDamage;
  private static String[] activities = {
    "walking", "standing", "attacking", "blocking1", "blocking2", "blocking3",
    "bashing", "slashing1", "slashing2", "slashing3"};

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
    u.takeDamage(dmg);
  }

}
