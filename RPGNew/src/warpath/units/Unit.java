package warpath.units;

import jwbgl.Posn;
import warpath.core.Direction;
import warpath.objects.GameObject;
import warpath.players.Player;
import warpath.ui.components.FloorOverlay;

/**
 * Created by Jack on 8/20/2016.
 */
public interface Unit extends GameObject {
  Posn getPosn();
  int getCurrentHP();
  int getMaxHP();
  int getCurrentEP();
  int getMaxEP();
  Player getPlayer();
  void doUpkeep();
  void doEvents();
  Unit getTargetUnit();
  Posn getTargetPosn();
  void setCurrentActivity(String activity);
  void setTargetUnit(Unit u);
  void setTargetPosn(Posn p);
  FloorOverlay getFloorOverlay();
  void setFloorOverlay(FloorOverlay o);
  void updateFloorOverlay();
  void setTargetPosnOverlay(Posn o);
  String getCurrentActivity();
  String getNextActivity();
  void setNextActivity(String activity);
  void setNextTargetPosn(Posn p);
  int getBlockCost();
  int getSlashCost();
  Direction getDirection();
  void pointAt(Posn nextPosn);
  void setNewSlashDirection(boolean b);
  void setDirection(Direction dir);
  Posn getNextTargetPosn();
  void setNextTargetUnit(Unit v);

  boolean isMoving();

  boolean isHostile(Unit playerUnit);

  void die();

  void takeHit(GameObject src, int damage);

  FloorOverlay getTargetPosnOverlay();

  void moveTo(Posn posn);
}
