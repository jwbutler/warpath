package warpath.units;

import jwbgl.Posn;

import warpath.core.Activity;
import warpath.animations.Animation;
import warpath.internals.Direction;
import warpath.objects.GameObject;
import warpath.players.Player;
import warpath.objects.FloorOverlay;

import java.util.List;

/**
 * @author jbutler
 */
public interface Unit extends GameObject {

  void doUpkeep();
  void doEvents();

  int getBlockCost();
  Activity getCurrentActivity();
  Animation getCurrentAnimation();
  int getCurrentEP();
  int getCurrentHP();
  FloorOverlay getFloorOverlay();
  int getMaxEP();
  int getMaxHP();
  Activity getNextActivity();
  Posn getNextTargetPosn();
  List<Posn> getPath();
  Player getPlayer();
  Posn getPosn();
  int getSlashCost();
  Posn getTargetPosn();
  FloorOverlay getTargetPosnOverlay();
  Unit getTargetUnit();

  void setCurrentActivity(Activity activity);
  void setFloorOverlay(FloorOverlay o);
  void setTargetPosn(Posn p);
  void setTargetUnit(Unit u);
  void setTargetPosnOverlay(Posn o);
  void setNextActivity(Activity activity);
  void setNextTargetPosn(Posn p);
  Direction getDirection();
  void pointAt(Posn nextPosn);
  void setSlashDirectionIsNew(boolean b);
  void setDirection(Direction dir);
  void setNextTargetUnit(Unit v);

  void die();
  void moveBy(int dx, int dy);
  void moveBy(Posn posn);
  void moveTo(Posn posn);
  boolean isHostile(Unit playerUnit);
  boolean isMoving();
  void takeDamage(int dmg);
  void takeHit(GameObject src, int damage);
  void takeBashHit(GameObject src, int dmg);
  void takeSlashHit(GameObject src, int dmg);
  void updateFloorOverlay();
}
