import jwbgl.*;
public class TestLevel extends Level {
  public TestLevel(RPG game) {
    super(game, 15, 15);
    // Add some player units.
    //HumanUnit u = new HumanUnit(me, "u", new Posn(3,4), me.getHumanPlayer());
    //SwordGirl u = new SwordGirl(me, "u", new Posn(3,4), me.getHumanPlayer());
    
    // Make a hostile AI player

    // Make an "enemy" guy
    
    //HumanUnit x = new HumanUnit(me, "x", new Posn(9,4), me.getPlayer(2));
    //WanderingUnit x = new WanderingUnit(me, "x", new Posn(9,4), me.getPlayer(2));
    
    /* These were EnemySwordGuys */
    Unit x = new EnemySwordGuy(game, "x", new Posn(6,4), game.getPlayer(2));
    units.add(x);
    /*Unit y = new EnemySwordGuy(game, "y", new Posn(9,6), game.getPlayer(2));
    units.add(y);
    Unit z = new EnemySwordGuy(game, "z", new Posn(9,8), game.getPlayer(2));
    units.add(z);*/
    
    objects.add(new Wall(game, new Posn(8,10), "wall_48x78_1.png"));
    objects.add(new Wall(game, new Posn(9,10), "wall_48x78_1.png"));
    objects.add(new Wall(game, new Posn(10,10), "wall_48x78_1.png"));
    objects.add(new Wall(game, new Posn(11,10), "wall_48x78_1.png"));
  }
}
