package mazeobject.powerup.cannon;

import java.awt.Point;

import mazeobject.ObjectType;

public class PlasmaCannon extends Cannon {
  public PlasmaCannon(double speed, int damage) {
    super(0, speed, damage);
  }
  
  public PlasmaCannon(Point location, double speed, int damage) {
    super(location, 0, speed, damage);
  }
  
  public PlasmaCannon(int reload, double speed, int damage) {
    super(reload, speed, damage);
  }
  
  public PlasmaCannon(Point location, int reload, double speed, int damage) {
    super(location, reload, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.PlasmaCannon;
  }
  
  @Override
  public ObjectType getWeaponType() {
    return ObjectType.Plasma;
  }
  
  @Override
  public int getEnergyCost() {
    return 1;
  }
  
  @Override
  public int shotsPerMove() {
    return 2;
  }
  
  @Override
  public String getMessageSubstring() {
    return "Plasma Cannon";
  }
  
  @Override
  public String getShootingSound(boolean from_ship) {
    if (from_ship) {
      return "weapons/plasma.wav";
    }
    return "weapons/laser06.wav";
  }
}
