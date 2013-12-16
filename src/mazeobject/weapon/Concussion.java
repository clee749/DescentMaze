package mazeobject.weapon;

import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class Concussion extends Weapon {
  public Concussion(MazeObject source, double col, double row, CellSide direction, double speed, int damage) {
    super(source, col, row, direction, speed, damage);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Concussion;
  }
  
  @Override
  public boolean doesSplashDamage() {
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
}
