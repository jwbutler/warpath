package warpath.units;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Random;

import jwbgl.*;
import warpath.core.RPG;
import warpath.items.ItemFactory;
import warpath.items.Shield;
import warpath.items.Sword;
import warpath.players.Player;

/**
 * Warrior-type player unit class.
 * Sword/shield.
 */
public class SwordGuy extends HumanUnit {
  private static final long serialVersionUID = 1L;
  private int minDamage, maxDamage, bashDamage, slashDamage;
  private static String[] activities = {
    "walking", "standing", "attacking", "blocking_1", "blocking_2", "blocking_3",
    "bashing", "slashing_1", "slashing_2", "slashing_3", "falling"};

  public SwordGuy(String name, Posn posn, Player player, HashMap<Color, Color> paletteSwaps) {
    super(name, activities, paletteSwaps, posn, player);
    currentHP = maxHP = 200;
    currentEP = maxEP = 100;
    minDamage = 6;
    maxDamage = 12;
    bashDamage = 20;
    slashDamage = 6;
    addAccessory(ItemFactory.create(this, "Sword"));
    addAccessory(ItemFactory.create(this, "Shield"));
    //addAccessory(new Sword(game, this, "sword"));
    //addAccessory(new Shield(game, this, "shield2"));
  }
  public SwordGuy(String name, Posn posn, Player player) {
    this(name, posn, player, new HashMap<Color, Color>());
  }

  public void doAttackHit(Unit u) {
    int dmg = RPG.getInstance().getRNG().nextInt(maxDamage - minDamage) + minDamage + 1;
    // soundFX
    u.takeHit(this, dmg);
    playHitSound();
  }
  public void doBashHit(Unit u) {
    RPG game = RPG.getInstance();
    int dx = u.getX() - getX();
    int dy = u.getY() - getY();
    int x = u.getX()+dx;
    int y = u.getY()+dy;
    if (game.getFloor().contains(new Posn(x,y)) && !game.isObstacle(new Posn(x,y))) {
      u.move(dx, dy);
    } else {
      System.out.println("bashfail");
    }
    u.takeBashHit(this, bashDamage);
    playHitSound();
    //System.out.println(this + " hit unit " + u);
  }
  
  public void doSlashHit(Unit u) {
    RPG game = RPG.getInstance();
    int dx = u.getX() - getX();
    int dy = u.getY() - getY();
    int x = u.getX()+dx;
    int y = u.getY()+dy;
    if (!game.isObstacle(new Posn(x,y))) {
      u.move(dx, dy);
    }
    u.takeSlashHit(this, slashDamage);
    playHitSound();
    //System.out.println(this + " hit unit " + u);
  }
  
  @Override
  public void draw(Graphics g) {
    super.draw(g);
  }
  
  @Override
  public void doEvents() {
    //printDebug();
    super.doEvents();
  }
}
