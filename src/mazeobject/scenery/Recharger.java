package mazeobject.scenery;

import java.awt.Point;
import java.util.ArrayList;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class Recharger extends Scenery {
  public Recharger(Point location) {
    super(location);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Recharger;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    for (Pyro ship : ships) {
      if (location.equals(ship.getLocation())) {
        ship.recharge();
      }
    }
    return null;
  }
}
