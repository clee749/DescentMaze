package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class HomingPack extends Powerup {
  public HomingPack(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.HomingPack;
  }
}
