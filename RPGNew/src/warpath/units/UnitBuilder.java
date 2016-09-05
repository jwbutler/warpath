package warpath.units;

import jwbgl.Posn;

import warpath.activities.Activity;
import warpath.animations.Animation;
import warpath.items.Accessory;
import warpath.items.ItemSlot;
import warpath.players.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESIGN PATTERNS LOL
 * @author jbutler
 * @since September 2016
 */
public abstract class UnitBuilder<T extends Unit> {

  public List<Animation> animations = new ArrayList<>();
  public Map<ItemSlot, Accessory> equipment = new HashMap<>();
  public Map<Color, Color> paletteSwaps = new HashMap<>();
  public List<Activity> activities = new ArrayList<>();
  public String name = null;
  public String spriteName = null;
  public Posn posn = null;
  public Player player = null;

  public UnitBuilder() {
  }

  public UnitBuilder player(Player player) {
    this.player = player;
    return this;
  }

  public UnitBuilder name(String name) {
    this.name = name;
    return this;
  }

  public UnitBuilder spriteName(String spriteName) {
    this.spriteName = spriteName;
    return this;
  }

  public UnitBuilder animations(List<Animation> animations) {
    this.animations = animations;
    return this;
  }

  public UnitBuilder activities(List<Activity> activities) {
    this.activities = activities;
    return this;
  }

  public UnitBuilder paletteSwaps(Map<Color, Color> paletteSwaps) {
    this.paletteSwaps = paletteSwaps;
    return this;
  }

  public UnitBuilder equipment(Map<ItemSlot, Accessory> equipment) {
    this.equipment = equipment;
    return this;
  }

  public UnitBuilder posn(Posn p) {
    this.posn = p;
    return this;
  }
}
