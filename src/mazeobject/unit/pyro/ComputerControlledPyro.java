package mazeobject.unit.pyro;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.MultipleObject;
import mazeobject.ObjectType;
import mazeobject.transients.Explosion;
import mazeobject.unit.Unit;
import mazeobject.weapon.Weapon;
import pilot.AutoPilot;
import pilot.MazeWalker;
import pilot.PyroPilot;

public class ComputerControlledPyro extends Pyro {
  private MazeWalker solver;
  private PyroPilot pilot;
  
  public ComputerControlledPyro(MazeEngine engine, Point location, CellSide direction) {
    super(engine, location, direction);
  }
  
  public void setSolverAndPilot(MazeWalker solver) {
    this.solver = solver;
    pilot = new AutoPilot(solver);
  }
  
  public MazeWalker getSolver() {
    return solver;
  }
  
  @Override
  public boolean atExit(MazeUtility maze) {
    return (pilot instanceof MazeWalker && !exploded && solver.atExit());
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    action_reload_left = -1;
    handleSpecialStateExpiration();
    handleMessageExpiration();
    MazeObject created = reactToFront(maze, engine, ships);
    if (created == null) {
      --reload_left;
      if (!cloaked && !invulnerable) {
        created = reactToRear(maze, engine, ships);
      }
    }
    if (created == null && reload_left < 0) {
      useSafeCannon();
    }
    return created;
  }
  
  @Override
  public void makeNextMove() {
    if (spawning) {
      spawning = false;
      return;
    }
    location.x = next_location.x;
    location.y = next_location.y;
    row = location.y;
    col = location.x;
    if (atExit(null)) {
      return;
    }
    CellSide next_direction = pilot.makeNextMove();
    if (next_direction == null) {
      if (pilot instanceof AutoPilot) {
        pilot = solver;
        next_direction = pilot.makeNextMove();
      }
      else {
        setVelocity(0, 0);
        return;
      }
    }
    next_location = MazeWalker.getLocationInDirection(location.x, location.y, next_direction);
    if (!next_direction.equals(direction)) {
      setDirection(next_direction);
    }
    setVelocity(next_direction);
  }
  
  public MazeObject reactToFront(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    boolean missile_ready = missile_reload < 1;
    --missile_reload;
    Point current = new Point(location);
    int distance = 0;
    boolean missile_fired = false;
    MazeObject shots = null;
    while (maze.isValidCell(current.x, current.y)) {
      for (Pyro ship : ships) {
        if (!ship.equals(this) && !ship.isDestroyed() && ship.isVisible()
                && ship.getLocation().equals(current)) {
          return null;
        }
      }
      if (engine.containsVisibleRobot(current)) {
        shots = fireCannon(engine);
        if (shots == null && reload_left < 1 && distance > 1) {
          if (missile_ready) {
            return substituteMissile();
          }
          break;
        }
        if (shots != null) {
          action_reload_left = action_reload;
        }
        if (missile_ready && distance > 1) {
          CellSide[] adjacents = CellSide.adjacents(direction);
          if (engine.containsUncloakedStrongRobot(current)) {
            if (!maze.hasWall(current, adjacents[0]) || !maze.hasWall(current, adjacents[1])) {
              Weapon missile = fireMissile(ObjectType.Homing);
              if (missile != null) {
                shots = addShot((MultipleObject) shots, missile);
                missile_fired = true;
              }
            }
            if (!missile_fired) {
              Weapon missile = fireMissile(ObjectType.Concussion);
              if (missile != null) {
                shots = addShot((MultipleObject) shots, missile);
                missile_fired = true;
              }
            }
          }
        }
        return shots;
      }
      if (maze.hasWall(current.x, current.y, direction) && !maze.hasBarrier(current.x, current.y, direction)) {
        if (missile_ready && distance > 2) {
          CellSide[] dirs = CellSide.adjacents(direction);
          Point left = MazeWalker.getLocationInDirection(current.x, current.y, dirs[0]);
          boolean found_left =
                  maze.isValidCell(left.x, left.y) && !maze.hasWall(current, dirs[0])
                          && engine.containsUncloakedStrongRobot(left);
          Point right = MazeWalker.getLocationInDirection(current.x, current.y, dirs[1]);
          boolean found_right =
                  maze.isValidCell(right.x, right.y) && !maze.hasWall(current, dirs[1])
                          && engine.containsUncloakedStrongRobot(right);
          if (found_left || found_right) {
            Weapon missile = fireMissile(ObjectType.Smart);
            if (missile != null) {
              shots = addShot((MultipleObject) shots, missile);
              missile_fired = true;
            }
          }
        }
        break;
      }
      current = MazeWalker.getLocationInDirection(current.x, current.y, direction);
      ++distance;
    }
    return shots;
  }
  
  public MazeObject reactToRear(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    boolean bomb_ready = bomb_reload < 1;
    --bomb_reload;
    if (bomb_ready && num_bombs > 0 && maze.isValidCell(next_location.x, next_location.y)
            && !maze.hasWall(next_location, direction)) {
      Point current = new Point(location);
      int distance = 0;
      CellSide dir = CellSide.opposite(direction);
      while (maze.isValidCell(current.x, current.y)) {
        for (Pyro ship : ships) {
          if (!ship.equals(this) && !ship.isDestroyed() && ship.isVisible()
                  && ship.getLocation().equals(current)) {
            return null;
          }
        }
        if (engine.containsVisibleRobot(current)) {
          if (distance > 1) {
            Iterator<Unit> it = engine.getRobots(current);
            while (it.hasNext()) {
              Unit unit = it.next();
              if (!unit.getType().equals(ObjectType.Proximity) && unit.isVisible()) {
                return dropBomb();
              }
            }
          }
          return null;
        }
        if (maze.hasWall(current.x, current.y, dir) && !maze.hasBarrier(current.x, current.y, dir)) {
          break;
        }
        current = MazeWalker.getLocationInDirection(current.x, current.y, dir);
        ++distance;
      }
    }
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    if (shields < 0) {
      if (!exploded) {
        exploded = true;
        return new Explosion(col, row, 2 * num_transitions, 0.95);
      }
      --survival_left;
      if (survival_left <= 0) {
        is_destroyed = true;
      }
      return null;
    }
    row += (double) dxdy.y / num_transitions;
    col += (double) dxdy.x / num_transitions;
    if (exploded) {
      return null;
    }
    rechargeEnergy();
    handleScoreIncrementExpiration();
    handleMissileLock();
    --action_reload_left;
    if (action_reload_left == 0) {
      return fireCannon(engine);
    }
    return null;
  }
  
  public Weapon substituteMissile() {
    if (num_concussions > 0) {
      return fireMissile(ObjectType.Concussion);
    }
    if (num_homings > 0) {
      return fireMissile(ObjectType.Homing);
    }
    return fireMissile(ObjectType.Smart);
  }
  
  public void useSafeCannon() {
    if (energy < 80) {
      switchCannon(ObjectType.LaserCannon, false);
    }
    else if (energy > 80 && energy < 120) {
      if (!switchCannon(ObjectType.PlasmaCannon, false)) {
        switchCannon(ObjectType.FusionCannon, false);
      }
    }
    else if (energy > 120) {
      if (!switchCannon(ObjectType.FusionCannon, false)) {
        switchCannon(ObjectType.PlasmaCannon, false);
      }
    }
  }
}
