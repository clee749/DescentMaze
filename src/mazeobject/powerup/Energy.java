package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class Energy extends Powerup {
  public Energy(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Energy;
  }
}
