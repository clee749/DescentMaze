package mazeobject.scenery;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.transients.SpawningUnit;
import mazeobject.unit.pyro.Pyro;

public class Entrance extends Scenery {
  private final LinkedList<Pyro> spawn_list;
  private int spawn_reload;
  
  public Entrance(Point location) {
    super(location);
    spawn_list = new LinkedList<Pyro>();
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Entrance;
  }
  
  public LinkedList<Pyro> getSpawnList() {
    return spawn_list;
  }
  
  public void addSpawningShip(Pyro ship) {
    spawn_list.add(ship);
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    if (!spawn_list.isEmpty()) {
      if (spawn_reload <= 0) {
        Pyro ship = spawn_list.removeFirst();
        ship.spawn(new Point(location), CellSide.South);
        spawn_reload = 6;
        engine.playSound("effects/mtrl01.wav", location);
        return new SpawningUnit(location, MazeEngine.NUM_SHIFTS, ship);
      }
      --spawn_reload;
    }
    return null;
  }
}
