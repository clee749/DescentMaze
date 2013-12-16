package mazeobject.scenery;

import java.awt.Point;
import java.util.ArrayList;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class Exit extends Scenery {
  public Exit(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Exit;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
}
