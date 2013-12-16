package mazeobject.powerup.cannon;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.unit.Unit;
import mazeobject.weapon.Weapon;
import mazeobject.weapon.WeaponFactory;

public abstract class Cannon extends Powerup {
  protected int reload;
  protected double weapon_speed;
  protected int weapon_damage;
  
  public Cannon(int reload, double weapon_speed, int weapon_damage) {
    this(null, reload, weapon_speed, weapon_damage);
  }
  
  public Cannon(Point location, int reload, double weapon_speed, int weapon_damage) {
    super(location);
    this.reload = reload;
    this.weapon_speed = weapon_speed;
    this.weapon_damage = weapon_damage;
  }
  
  public int getReload() {
    return reload;
  }
  
  public int shotsPerMove() {
    return 1;
  }
  
  public double getWeaponSpeed() {
    return weapon_speed;
  }
  
  public void setWeaponSpeed(double weapon_speed) {
    this.weapon_speed = weapon_speed;
  }
  
  public int getWeaponDamage() {
    return weapon_damage;
  }
  
  public void setWeaponDamage(int weapon_damage) {
    this.weapon_damage = weapon_damage;
  }
  
  public Weapon shoot(Unit source, double col, double row, CellSide direction) {
    return WeaponFactory.newWeapon(getWeaponType(), col, row, direction, source, weapon_speed, weapon_damage);
  }
  
  public void releaseAsPowerup(Point location) {
    this.location = location;
    is_destroyed = false;
  }
  
  public static String getMessageSubstring(ObjectType type) {
    Cannon cannon;
    switch (type) {
      case LaserCannon:
        cannon = new LaserCannon(0.0, 0, 0);
        break;
      case PlasmaCannon:
        cannon = new PlasmaCannon(0.0, 0);
        break;
      case FusionCannon:
        cannon = new FusionCannon(0.0, 0);
      default:
        return type.name();
    }
    return cannon.getMessageSubstring();
  }
  
  public abstract ObjectType getWeaponType();
  
  public abstract int getEnergyCost();
  
  public abstract String getMessageSubstring();
  
  public abstract String getShootingSound(boolean from_ship);
}
