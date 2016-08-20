package warpath.activities;

import jwbgl.Posn;
import warpath.units.BasicUnit;
import warpath.units.Unit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jack on 6/12/2016.
 */
public class Activity {
  private final Unit sourceUnit;
  private final Unit targetUnit;
  private final Posn targetPosn;
  private final int length;
  private final ActivityHandler queueHandler;
  private final ActivityHandler startHandler;
  private final ActivityHandler endHandler;
  private final Map<Integer, ActivityHandler> handlers; // When should these be executed?

  public static class Builder {
    private Unit sourceUnit;
    private Unit targetUnit;
    private Posn targetPosn;
    private int length;
    private ActivityHandler queueHandler = null;
    private ActivityHandler startHandler = null;
    private ActivityHandler endHandler = null;
    private Map<Integer, ActivityHandler> handlers = new HashMap<>();
    public Builder() {
    }
    public Builder sourceUnit(Unit u) {
      sourceUnit = u;
      return this;
    }
    public Builder targetUnit(Unit u) {
      targetUnit = u;
      return this;
    }
    public Builder targetPosn(Posn p) {
      targetPosn = p;
      return this;
    }
    public Builder length(int l) {
      length = l;
      return this;
    }
    public Builder queueHandler(ActivityHandler handler) {
      queueHandler = handler;
      return this;
    }
    public Builder startHandler(ActivityHandler handler) {
      startHandler = handler;
      return this;
    }
    public Builder endHandler(ActivityHandler handler) {
      endHandler = handler;
      return this;
    }
    public Builder setHandler(int index, ActivityHandler handler) {
      handlers.put(index, handler);
      return this;
    }
    public Activity build() {
      return new Activity(this);
    }
  }

  private Activity(Builder builder) {
    this.sourceUnit = builder.sourceUnit;
    this.targetUnit = builder.targetUnit;
    this.targetPosn = builder.targetPosn;
    this.length = builder.length;
    this.handlers = builder.handlers;
    this.queueHandler = builder.queueHandler;
    this.startHandler = builder.startHandler;
    this.endHandler = builder.endHandler;
  }

  public Unit getSourceUnit() {
    return sourceUnit;
  }

  public Unit getTargetUnit() {
    return targetUnit;
  }

  public Posn getTargetPosn() {
    return targetPosn;
  }

  public int getLength() {
    return length;
  }
}
