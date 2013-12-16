package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class ConcussionPack extends Powerup {
  public ConcussionPack(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.ConcussionPack;
  }
}
