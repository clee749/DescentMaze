package mazeobject.weapon;

import java.awt.Image;

import maze.CellSide;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import util.ImageHandler;

public class Laser extends Weapon {
  private final int level;
  
  public Laser(MazeObject source, double col, double row, CellSide direction, double speed, int damage,
          int level) {
    super(source, col, row, direction, speed, damage);
    this.level = level;
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Laser;
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return images.getImage(getType().name() + String.valueOf(level) + direction.name());
  }
}
