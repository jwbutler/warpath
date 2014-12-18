  /* This is the file where we actually create the game parameters: adding
   * players, units, etc. to the game.  We can define different ones for
   * various levels, modes, whatever. */

public class RPGDriver {

  public static void main(String[] args) {
    //CreatorPanel cc = new CreatorPanel(800, 600);
    /*CharacterCreator cc = new CharacterCreator();
    return;*/
    RPG me = new RPG();
    // Add some player units.
    //HumanUnit u = new HumanUnit(me, "u", new Posn(3,4), me.getHumanPlayer());
    SwordGuy u = new SwordGuy(me, "u", new Posn(3,4), me.getHumanPlayer());
    //SwordGirl u = new SwordGirl(me, "u", new Posn(3,4), me.getHumanPlayer());
    me.addUnit(u);
    
    // Make a hostile AI player
    me.addPlayer(2, new AIPlayer());
    me.getHumanPlayer().setHostile(me.getPlayer(2));
    me.getPlayer(2).setHostile(me.getPlayer(1));
    
    // Make an "enemy" guy
    
    //HumanUnit x = new HumanUnit(me, "x", new Posn(9,4), me.getPlayer(2));
    //WanderingUnit x = new WanderingUnit(me, "x", new Posn(9,4), me.getPlayer(2));
    EnemySwordGuy x = new EnemySwordGuy(me, "x", new Posn(9,4), me.getPlayer(2));
    me.addUnit(x);
    //EnemySwordGuy y = new EnemySwordGuy(me, "y", new Posn(9,5), me.getPlayer(2));
    //me.addUnit(y);
    
    me.addObject(new Wall(me, new Posn(9,7), "wall_48x78_1.png"));
    me.addObject(new Wall(me, new Posn(9,8), "wall_48x78_1.png"));
    me.addObject(new Wall(me, new Posn(9,9), "wall_48x78_1.png"));
    me.addObject(new Wall(me, new Posn(9,10), "wall_48x78_1.png"));
    me.start();
  }
}
