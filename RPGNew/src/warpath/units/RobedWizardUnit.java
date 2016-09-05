package warpath.units;
import java.awt.Color;
import java.io.Serializable;
import java.util.*;

import jwbgl.*;

import warpath.activities.Activity;
import warpath.animations.Animation;
import warpath.animations.AnimationTemplates;
import warpath.internals.Direction;
import warpath.core.RPG;
import warpath.items.Accessory;
import warpath.objects.Corpse;
import warpath.players.Player;

public abstract class RobedWizardUnit extends BasicUnit implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final List<Activity> ACTIVITIES = Arrays.asList(
    Activity.APPEARING, Activity.FALLING, Activity.REZZING, Activity.STANDING,
    Activity.STUNNED_SHORT, Activity.STUNNED_LONG, Activity.TELEPORTING, Activity.WALKING
  );
  public static final int TELEPORT_COST = 200;
  public static final int REZ_COST = 200;
  
  public RobedWizardUnit(String name, String spriteName, List<Activity> activities, Map<Color, Color> paletteSwaps,
  Posn posn, Player player) {
    super(name, spriteName, activities, paletteSwaps, posn, player);
    setOffsets(0, -32);
    setHPBarOffset(-40);
  }
  
  public RobedWizardUnit(String name, List<Activity> activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "robed_wizard", activities, new HashMap<>(), posn, player);
  }  
  
  public RobedWizardUnit(String name, List<Activity> activities, Map<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(name, "robed_wizard", activities, paletteSwaps, posn, player);
  }  
  
  public RobedWizardUnit(String name, Posn posn, Player player) {
    this(name, ACTIVITIES, posn, player);
  }

  @Override
  public void die() {
    super.die();
    RPG game = RPG.getInstance();
    game.addObject(new Corpse(getPosn(), "robed_wizard_dead.png"));
  }
  
  @Override
  public void loadAnimations() {
    long t = System.currentTimeMillis();
    for (Activity activity : getActivity()) {
      loadActivityAnimations(activity);
    }
    
    for (Accessory e: getEquipment().values()) {
      e.loadAnimations();
    }
    t = System.currentTimeMillis() - t;
    System.out.printf("%s.loadAnimations(): %d ms\n", this.getClass(), t);
  }
  
  @Override
  // We can probably optimize this further but this is decently concise now.
  public void loadActivityAnimations(Activity activity) {
    if (activity.equals(Activity.FALLING)) {
      loadFallingAnimations();
    } else if (activity.equals(Activity.STANDING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STANDING);
    } else if (activity.equals(Activity.WALKING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_WALKING);
    } else if (activity.equals(Activity.TELEPORTING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_TELEPORTING);
    } else if (activity.equals(Activity.APPEARING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_APPEARING);
    } else if (activity.equals(Activity.REZZING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_REZZING);
    } else if (activity.equals(Activity.STUNNED_SHORT)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STUNNED_SHORT);
    } else if (activity.equals(Activity.APPEARING)) {
      loadGenericAnimations(activity, AnimationTemplates.WIZARD_STUNNED_LONG);
    } else {
      loadGenericAnimations(activity);
    }
  }

  public void loadFallingAnimations() {
    for (Direction direction : Direction.directions()) {
      getAnimations().add(Animation.fromTemplate(
        getSpriteName(),
        Activity.FALLING,
        direction,
        AnimationTemplates.WIZARD_FALLING,
        getFrameCache())
      );
    }
  }
}
