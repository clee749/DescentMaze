package mazeobject.scenery;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.transients.SpawningUnit;
import mazeobject.unit.pyro.Pyro;
import mazeobject.unit.robot.RobotFactory;

public class Generator extends Scenery {
  private final ObjectType robot;
  private final boolean different_ships;
  private HashSet<Pyro> ships_seen;
  private int supplies;
  private final int long_recharge;
  private int long_recharge_left;
  private final int robots;
  private int robots_left;
  private final int recharge;
  private int recharge_left;
  
  public Generator(Point location, MazeUtility maze, ObjectType robot, CellSide direction,
          boolean different_ships) {
    super(location);
    this.direction = direction;
    this.robot = robot;
    this.different_ships = different_ships;
    if (different_ships) {
      ships_seen = new HashSet<Pyro>();
    }
    supplies = 3;
    long_recharge = 10;
    robots = 4;
    recharge = 2;
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Generator;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    if (long_recharge_left <= 0 && supplies > 0 && robots_left <= 0) {
      for (Pyro ship : ships) {
        if (!ship.isDestroyed() && (!different_ships || !ships_seen.contains(ship))
                && location.distanceSq(ship.getLocation()) < 9) {
          robots_left = robots;
          recharge_left = 0;
          --supplies;
          if (different_ships) {
            ships_seen.add(ship);
          }
          break;
        }
      }
    }
    else {
      --long_recharge_left;
    }
    if (robots_left > 0) {
      if (recharge_left <= 0) {
        --robots_left;
        recharge_left = recharge;
        long_recharge_left = long_recharge;
        engine.playSound("effects/mtrl01.wav", location);
        return new SpawningUnit(location, MazeEngine.NUM_SHIFTS, RobotFactory.newRobot(robot, location,
                direction));
      }
      else {
        --recharge_left;
      }
    }
    return null;
  }
}
