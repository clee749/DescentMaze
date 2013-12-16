package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class ConcussionMissile extends Powerup {
  public ConcussionMissile(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.ConcussionMissile;
  }
}
