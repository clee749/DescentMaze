package mazeobject.unit.robot;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.transients.Explosion;
import mazeobject.unit.Unit;
import mazeobject.unit.pyro.Pyro;
import mazeobject.weapon.Weapon;
import pilot.MazeWalker;
import util.ImageHandler;

public abstract class Robot extends Unit {
  protected int cannon_side;
  protected int move_left;
  protected boolean exploring;
  protected CellSide explore_direction;
  protected boolean score_dispensed;
  
  public Robot(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon_side = (int) (Math.random() * 2);
    move_left = -1;
  }
  
  public CellSide seesLocation(Point current, Point target, MazeUtility maze) {
    CellSide direction;
    if (target.x == current.x) {
      if (target.y - current.y < 0) {
        direction = CellSide.North;
      }
      else {
        direction = CellSide.South;
      }
    }
    else if (target.y == current.y) {
      if (target.x - current.x < 0) {
        direction = CellSide.West;
      }
      else {
        direction = CellSide.East;
      }
    }
    else {
      return null;
    }
    Point loc = new Point(current);
    while (!loc.equals(target)) {
      Point next = MazeWalker.getLocationInDirection(loc.x, loc.y, direction);
      if (maze.hasWall(loc.x, loc.y, direction)) {
        if (!maze.hasBarrier(loc.x, loc.y, direction)) {
          return null;
        }
      }
      loc = next;
    }
    return direction;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    if (exploded) {
      return null;
    }
    handleMovement(engine);
    MazeObject created = reactToShips(maze, engine, ships);
    if (created != null) {
      return created;
    }
    if (move_left <= 0) {
      nextMovement(maze, engine);
    }
    --reload_left;
    return null;
  }
  
  public void handleMovement(MazeEngine engine) {
    --move_left;
    action_reload_left = -1;
    if (move_left == 0) {
      dxdy.x = 0;
      dxdy.y = 0;
      engine.robotMoved(this, location, false);
      location = next_location;
      next_location = null;
    }
  }
  
  public MazeObject reactToShips(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    for (Pyro ship : ships) {
      if (ship.isVisible() && !ship.isDestroyed()) {
        CellSide dir;
        if (ship.getLocation().equals(location)) {
          dir = CellSide.bestDirection(col, row, ship.getCol(), ship.getRow(), direction);
        }
        else {
          dir = seesLocation(location, ship.getLocation(), maze);
          if (next_location != null && dir == null) {
            dir = seesLocation(next_location, ship.getLocation(), maze);
          }
        }
        if (dir != null) {
          exploring = false;
          explore_direction = null;
          setDirection(dir);
          if (move_left <= 0) {
            if (ship.getDirection().equals(CellSide.opposite(dir))) {
              dodgeShip(maze, engine, dir);
            }
            else if (canMoveInDirection(maze, engine, dir, true)) {
              dxdy = CellSide.dxdy(dir);
            }
            if (dxdy.x != 0 || dxdy.y != 0) {
              moved(engine);
            }
          }
          if (reload_left <= 0) {
            action_reload_left = action_reload;
            return fireCannon(engine);
          }
          break;
        }
      }
    }
    return null;
  }
  
  public void nextMovement(MazeUtility maze, MazeEngine engine) {
    if (exploring) {
      explore(maze, engine);
      if (Math.random() < 0.1) {
        exploring = false;
        explore_direction = null;
      }
    }
    else if (Math.random() < 0.1) {
      exploring = true;
      explore(maze, engine);
    }
    else if (engine.numRobots(location) > 1) {
      dodgeRobot(maze, engine);
    }
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    if (shields < 0) {
      if (!exploded) {
        exploded = true;
        engine.playSound("weapons/explode1.wav", location);
        return new Explosion(col, row, (int) ((Math.random() + 1) * MazeEngine.NUM_SHIFTS), 0.9);
      }
      --survival_left;
      if (survival_left <= 0) {
        is_destroyed = true;
        return null;
      }
    }
    row += (double) dxdy.y / num_transitions / getInverseSpeed();
    col += (double) dxdy.x / num_transitions / getInverseSpeed();
    if (exploded) {
      return null;
    }
    --action_reload_left;
    if (action_reload_left == 0) {
      action_reload_left = action_reload;
      return fireCannon(engine);
    }
    return null;
  }
  
  @Override
  public Weapon fireCannon(MazeEngine engine) {
    double offset = ((cannon_side % 2) * 2 - 1) * getCannonOffset();
    Point dxdy = CellSide.dxdy(CellSide.next(direction));
    ++cannon_side;
    reload_left = cannon.getReload();
    engine.playSound(cannon.getShootingSound(false), location);
    return cannon.shoot(this, col - offset * dxdy.x, row - offset * dxdy.y, direction);
  }
  
  @Override
  public void beDamaged(MazeEngine engine, int amount, MazeObject source, boolean is_splash) {
    shields -= amount;
    if (shields < 0 && !exploded) {
      survival_left = MazeEngine.NUM_SHIFTS / 3;
      if (!score_dispensed && source.getType().equals(ObjectType.Pyro)) {
        ((Pyro) source).incrementScore(getPoints());
      }
      score_dispensed = true;
    }
    if (!is_splash) {
      engine.playSound("weapons/explode1.wav", location);
    }
  }
  
  @Override
  public Powerup releasePowerup() {
    return getReleasedPowerup(new Point((int) Math.round(col), (int) Math.round(row)), Math.random());
  }
  
  public CellSide dodgeShip(MazeUtility maze, MazeEngine engine, CellSide avoid) {
    CellSide[] sides = CellSide.adjacents(avoid);
    boolean left = canMoveInDirection(maze, engine, sides[0], false);
    boolean right = canMoveInDirection(maze, engine, sides[1], false);
    if (left && right) {
      int index = (int) (Math.random() * 2);
      dxdy = CellSide.dxdy(sides[index]);
      return sides[index];
    }
    else if (left) {
      dxdy = CellSide.dxdy(sides[0]);
      return sides[0];
    }
    else if (right) {
      dxdy = CellSide.dxdy(sides[1]);
      return sides[1];
    }
    CellSide opposite = CellSide.opposite(avoid);
    if (canMoveInDirection(maze, engine, opposite, false)) {
      dxdy = CellSide.dxdy(opposite);
      return opposite;
    }
    return null;
  }
  
  public CellSide dodgeRobot(MazeUtility maze, MazeEngine engine) {
    ArrayList<CellSide> moves = new ArrayList<CellSide>();
    for (CellSide dir : CellSide.values()) {
      if (canMoveInDirection(maze, engine, dir, false)) {
        moves.add(dir);
      }
    }
    int index = (int) (Math.random() * (moves.size() + 1));
    if (index < moves.size()) {
      dxdy = CellSide.dxdy(moves.get(index));
      moved(engine);
      return moves.get(index);
    }
    return null;
  }
  
  public boolean canMoveInDirection(MazeUtility maze, MazeEngine engine, CellSide dir, boolean avoid_units) {
    Point loc = MazeWalker.getLocationInDirection(location, dir);
    return maze.isValidCell(loc.x, loc.y) && !maze.hasWall(location, dir)
            && (!avoid_units || !engine.isOccupied(loc));
  }
  
  public void moved(MazeEngine engine) {
    move_left = getInverseSpeed();
    next_location = new Point(location.x + dxdy.x, location.y + dxdy.y);
    engine.robotMoved(this, next_location, true);
  }
  
  public void explore(MazeUtility maze, MazeEngine engine) {
    ArrayList<CellSide> moves = new ArrayList<CellSide>();
    CellSide ignore = CellSide.opposite(explore_direction);
    for (CellSide dir : CellSide.values()) {
      if (dir.equals(ignore)) {
        continue;
      }
      if (canMoveInDirection(maze, engine, dir, false)) {
        moves.add(dir);
      }
    }
    if (moves.size() == 0) {
      exploring = false;
      if (explore_direction != null) {
        direction = CellSide.opposite(direction);
        explore_direction = null;
      }
      return;
    }
    CellSide chosen = moves.get((int) (Math.random() * moves.size()));
    dxdy = CellSide.dxdy(chosen);
    direction = chosen;
    explore_direction = chosen;
    moved(engine);
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int offset) {
    int x = (int) (center_cell_corner.x - side_length * (ship_location.x - col));
    int y = (int) (center_cell_corner.y - side_length * (ship_location.y - row));
    g.drawImage(getImage(images), x + offset, y + offset, null);
  }
  
  public abstract ThreatLevel getThreat();
  
  public abstract double getCannonOffset();
  
  public abstract int getInverseSpeed();
  
  public abstract Powerup getReleasedPowerup(Point location, double rand);
  
  public abstract int getPoints();
  
  public abstract String getGrowlSound();
}
