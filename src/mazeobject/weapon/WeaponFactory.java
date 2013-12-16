package mazeobject.weapon;

import maze.CellSide;
import mazeobject.MazeObject;
import mazeobject.ObjectType;

public class WeaponFactory {
  protected WeaponFactory() {
    
  }
  
  public static Weapon newWeapon(ObjectType type, double col, double row, CellSide direction,
          MazeObject source, double speed, int damage) {
    switch (type) {
      case Laser:
        return new Laser(source, col, row, direction, speed, damage, 1);
      case Plasma:
        return new Plasma(source, col, row, direction, speed, damage);
      case Fusion:
        return new Fusion(source, col, row, direction, speed, damage);
      case Fireball:
        return new Fireball(source, col, row, direction, speed, damage);
      case Concussion:
        return new Concussion(source, col, row, direction, speed, damage);
      case Homing:
        return new Homing(source, col, row, direction, speed, damage);
      case Smart:
        return new Smart(source, col, row, direction, speed, damage);
      case SmartPlasma:
        return new SmartPlasma(source, col, row, direction, speed, damage);
      default:
        return null;
    }
  }
  
  public static ObjectType[] weapons() {
    int start = ObjectType.Laser.ordinal();
    int size = ObjectType.Shield.ordinal() - start;
    ObjectType[] objects = ObjectType.values();
    ObjectType[] weapons = new ObjectType[size];
    for (int i = 0; i < size; ++i) {
      weapons[i] = objects[start + i];
    }
    return weapons;
  }
}
