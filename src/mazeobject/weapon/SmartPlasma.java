package mazeobject.weapon;

import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class SmartPlasma extends Weapon {
  public SmartPlasma(MazeObject source, double col, double row, CellSide direction, double speed, int damage) {
    super(source, col, row, direction, speed, damage);
  }
  
  public SmartPlasma(MazeObject source, double col, double row, CellSide direction, double speed, int damage,
          MazeUtility maze, MazeEngine engine) {
    this(source, col, row, direction, speed, damage);
    acquireTarget(maze, engine);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.SmartPlasma;
  }
  
  @Override
  public boolean doesSplashDamage() {
    return true;
  }
  
  @Override
  public boolean isHoming() {
    return true;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    MazeObject created = super.transitionAct(maze, engine, ships, num_transitions);
    if (created != null && created.getType().equals(ObjectType.Explosion)) {
      engine.playSound("weapons/explode1.wav", location);
    }
    return created;
  }
  
  public void acquireTarget(MazeUtility maze, MazeEngine engine) {
    CellSide[] dirs = CellSide.adjacents(direction);
    boolean left = acquireRobotTarget(maze, engine, dirs[0]);
    boolean right = acquireRobotTarget(maze, engine, dirs[1]);
    if ((left && right) || (!left && !right)) {
      direction = dirs[(int) (Math.random() * 2)];
    }
    else if (left) {
      direction = dirs[0];
    }
    else {
      direction = dirs[1];
    }
    dxdy = CellSide.dxdy(direction);
  }
}
