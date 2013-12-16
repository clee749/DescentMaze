package mazeobject.powerup;

import java.awt.Point;

import mazeobject.ObjectType;

public class ProximityPack extends Powerup {
  public ProximityPack(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.ProximityPack;
  }
}
