package mazeobject.weapon;

import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.MultipleObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class Smart extends Weapon {
  public Smart(MazeObject source, double col, double row, CellSide direction, double speed, int damage) {
    super(source, col, row, direction, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Smart;
  }
  
  @Override
  public boolean doesSplashDamage() {
    return true;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    MazeObject created = super.transitionAct(maze, engine, ships, num_transitions);
    if (is_destroyed) {
      engine.playSound("weapons/explode1.wav", location);
      MultipleObject objects = new MultipleObject();
      objects.add(created);
      CellSide[] dirs = CellSide.adjacents(direction);
      double center_row = row;
      double center_col = col;
      for (CellSide dir : dirs) {
        if (maze.hasWall(location.x, location.y, dir)) {
          Point offset = CellSide.dxdy(dir);
          center_row -= offset.y * 0.125;
          center_col -= offset.x * 0.125;
        }
      }
      ArrayList<CellSide> targets = new ArrayList<CellSide>();
      if (acquireRobotTarget(maze, engine, dirs[0])) {
        targets.add(dirs[0]);
      }
      if (acquireRobotTarget(maze, engine, dirs[1])) {
        targets.add(dirs[1]);
      }
      if (targets.isEmpty()) {
        targets.add(dirs[0]);
        targets.add(dirs[1]);
      }
      for (int i = 0; i < 10; ++i) {
        double plasma_row = center_row + Math.random() / 4 - 0.125;
        double plasma_col = center_col + Math.random() / 4 - 0.125;
        objects.add(new SmartPlasma(source, plasma_col, plasma_row,
                targets.get((int) (Math.random() * targets.size())), 1.0, 5));
      }
      engine.playSound("weapons/laser06.wav", location);
      return objects;
    }
    return created;
  }
}
