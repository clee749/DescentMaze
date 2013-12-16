package mazeobject.powerup.cannon;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.unit.Unit;
import mazeobject.weapon.Laser;
import mazeobject.weapon.Weapon;

public class LaserCannon extends Cannon {
  private int level;
  
  public LaserCannon(double speed, int damage, int level) {
    super(0, speed, damage);
    this.level = level;
  }
  
  public LaserCannon(int reload, double speed, int damage, int level) {
    super(reload, speed, damage);
    this.level = level;
  }
  
  public LaserCannon(Point location, int reload, double speed, int damage) {
    super(location, reload, speed, damage);
  }
  
  public LaserCannon(Point location, double speed, int damage) {
    super(location, 0, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.LaserCannon;
  }
  
  @Override
  public ObjectType getWeaponType() {
    return ObjectType.Laser;
  }
  
  @Override
  public int getEnergyCost() {
    return 1;
  }
  
  public int getLevel() {
    return level;
  }
  
  public boolean addLevel() {
    if (level < 4) {
      ++level;
      weapon_speed += 0.1;
      return true;
    }
    return false;
  }
  
  @Override
  public Weapon shoot(Unit source, double col, double row, CellSide direction) {
    return new Laser(source, col, row, direction, weapon_speed, weapon_damage, level);
  }
  
  @Override
  public String getMessageSubstring() {
    return "Laser Cannon Level " + level;
  }
  
  @Override
  public String getShootingSound(boolean from_ship) {
    if (from_ship) {
      switch (level) {
        case 1:
          return "weapons/laser03.wav";
        case 2:
          return "weapons/laser04.wav";
        case 3:
          return "weapons/laser02.wav";
        case 4:
          return "weapons/laser01.wav";
        default:
          return "weapons/laser07.wav";
      }
    }
    return "weapons/laser05.wav";
  }
}
