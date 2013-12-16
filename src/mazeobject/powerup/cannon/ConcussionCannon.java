package mazeobject.powerup.cannon;

import mazeobject.ObjectType;

public class ConcussionCannon extends Cannon {
  public ConcussionCannon(double speed, int damage) {
    super(2, speed, damage);
  }
  
  public ConcussionCannon(int reload, double speed, int damage) {
    super(reload, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.ConcussionCannon;
  }
  
  @Override
  public ObjectType getWeaponType() {
    return ObjectType.Concussion;
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
