package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class Shield extends Powerup {
  public Shield(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Shield;
  }
}
