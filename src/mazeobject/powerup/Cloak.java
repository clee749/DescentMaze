package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class Cloak extends Powerup {
  public Cloak(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Cloak;
  }
  
  @Override
  public String getAcquireSound() {
    return "effects/cloakon.wav";
  }
}
