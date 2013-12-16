package mazeobject.powerup.cannon;

import mazeobject.ObjectType;

public class HomingCannon extends Cannon {
  public HomingCannon(double speed, int damage) {
    super(2, speed, damage);
  }
  
  public HomingCannon(int reload, double speed, int damage) {
    super(reload, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.HomingCannon;
  }
  
  @Override
  public ObjectType getWeaponType() {
    return ObjectType.Homing;
  }
  
  @Override
  public int getEnergyCost() {
    return 0;
  }
  
  @Override
  public String getMessageSubstring() {
    return "";
  }
  
  @Override
  public String getShootingSound(boolean from_ship) {
    return "weapons/missile1.wav";
  }
}
