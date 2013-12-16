package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class SmartMissile extends Powerup {
  public SmartMissile(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.SmartMissile;
  }
}
