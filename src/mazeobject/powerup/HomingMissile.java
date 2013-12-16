package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class HomingMissile extends Powerup {
  public HomingMissile(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.HomingMissile;
  }
}
