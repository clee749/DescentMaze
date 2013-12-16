package mazeobject.powerup.cannon;

import mazeobject.ObjectType;

public class FireballCannon extends Cannon {
  public FireballCannon(double speed, int damage) {
    super(1, speed, damage);
  }
  
  public FireballCannon(int reload, double speed, int damage) {
    super(reload, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.FireballCannon;
  }
  
  @Override
  public ObjectType getWeaponType() {
    return ObjectType.Fireball;
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
    return "weapons/laser12.wav";
  }
}
