package mazeobject.weapon;

import maze.CellSide;
import mazeobject.MazeObject;
import mazeobject.ObjectType;

public class Plasma extends Weapon {
  public Plasma(MazeObject source, double col, double row, CellSide direction, double speed, int damage) {
    super(source, col, row, direction, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Plasma;
  }
}
