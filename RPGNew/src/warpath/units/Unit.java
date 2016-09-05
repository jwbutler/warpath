package warpath.units;

import jwbgl.Posn;

import warpath.activities.Activity;
import warpath.animations.Animation;
import warpath.internals.Direction;
import warpath.objects.GameObject;
import warpath.players.Player;
import warpath.ui.components.FloorOverlay;

import java.util.List;

/**
 * @author jbutler
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
  void setCurrentActivity(Activity activity);
  void setTargetUnit(Unit u);
  void setTargetPosn(Posn p);
  FloorOverlay getFloorOverlay();
  void setFloorOverlay(FloorOverlay o);
  void updateFloorOverlay();
  void setTargetPosnOverlay(Posn o);
  Activity getCurrentActivity();
  Activity getNextActivity();
  void setNextActivity(Activity activity);
  void setNextTargetPosn(Posn p);
  int getBlockCost();
  int getSlashCost();
  Direction getDirection();
  void pointAt(Posn nextPosn);
  void setSlashDirectionIsNew(boolean b);
  void setDirection(Direction dir);
  Posn getNextTargetPosn();
  void setNextTargetUnit(Unit v);

  boolean isMoving();

  boolean isHostile(Unit playerUnit);
  void die();
  FloorOverlay getTargetPosnOverlay();
  void moveTo(Posn posn);
  void moveBy(int dx, int dy);
  void takeDamage(int dmg);
  void takeBashHit(GameObject src, int dmg);
  void takeSlashHit(GameObject src, int dmg);
  void takeHit(GameObject src, int damage);

  Animation getCurrentAnimation();

  void checkNextTile();

  void moveBy(Posn posn);

  List<Posn> getPath();
}
