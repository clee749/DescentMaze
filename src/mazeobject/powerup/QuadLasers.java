package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class QuadLasers extends Powerup {
  public QuadLasers(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.QuadLasers;
  }
}
