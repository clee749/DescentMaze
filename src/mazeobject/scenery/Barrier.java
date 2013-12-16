package mazeobject.scenery;

import java.awt.Point;
import java.util.ArrayList;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class Barrier extends Scenery {
  public Barrier(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Barrier;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
}
