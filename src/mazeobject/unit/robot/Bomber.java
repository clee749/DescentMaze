package mazeobject.unit.robot;

import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Energy;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.ProximityPack;
import mazeobject.unit.Proximity;
import mazeobject.unit.pyro.Pyro;

public class Bomber extends Robot {
  public Bomber(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
  }
  
  public Bomber(Point location, CellSide direction) {
    this(location, direction, 29);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Bomber;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.High;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.0;
  }
  
  @Override
  public int getInverseSpeed() {
    return 1;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.10) {
      return new Energy(location);
    }
    if (rand < 0.20) {
      return new ProximityPack(location);
    }
    return null;
  }
  
  @Override
  public MazeObject reactToShips(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    for (Pyro ship : ships) {
      if (ship.isVisible() && !ship.isDestroyed()) {
        CellSide dir;
        if (ship.getLocation().equals(location)) {
          dir = CellSide.opposite(CellSide.direction(dxdy));
        }
        else {
          dir = seesLocation(location, ship.getLocation(), maze);
          if (next_location != null && dir == null) {
            dir = seesLocation(next_location, ship.getLocation(), maze);
          }
        }
        if (dir != null) {
          if (move_left <= 0) {
            CellSide moving = dodgeShip(maze, engine, dir);
            if (moving != null) {
              setDirection(moving);
            }
            if (dxdy.x != 0 || dxdy.y != 0) {
              moved(engine);
            }
          }
          if (reload_left <= 0) {
            reload_left = 10;
            engine.playSound("weapons/dropbomb.wav", location);
            return new Proximity(this, location, 27);
          }
          break;
        }
      }
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 200;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot01.wav";
  }
}
