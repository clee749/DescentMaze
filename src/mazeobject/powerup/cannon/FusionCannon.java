package mazeobject.powerup.cannon;

import java.awt.Point;

import mazeobject.ObjectType;

public class FusionCannon extends Cannon {
  public FusionCannon(double speed, int damage) {
    super(1, speed, damage);
  }
  
  public FusionCannon(Point location, double speed, int damage) {
    super(location, 1, speed, damage);
  }
  
  public FusionCannon(int reload, double speed, int damage) {
    super(reload, speed, damage);
  }
  
  public FusionCannon(Point location, int reload, double speed, int damage) {
    super(location, reload, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.FusionCannon;
  }
  
  @Override
  public ObjectType getWeaponType() {
    return ObjectType.Fusion;
  }
  
  @Override
  public int getEnergyCost() {
    return 3;
  }
  
  @Override
  public String getMessageSubstring() {
    return "Fusion Cannon";
  }
  
  @Override
  public String getShootingSound(boolean from_ship) {
    return "weapons/fusion.wav";
  }
}
