package mazeobject.weapon;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.transients.Explosion;
import mazeobject.unit.Unit;
import mazeobject.unit.pyro.Pyro;
import pilot.MazeWalker;
import util.ImageHandler;

public abstract class Weapon extends MazeObject {
  protected MazeObject source;
  protected Point dxdy;
  protected double col;
  protected double row;
  protected double speed;
  protected int damage;
  
  public Weapon(MazeObject source, Point origin, double speed, int damage) {
    super(origin);
    this.source = source;
  }
  
  public Weapon(MazeObject source, double col, double row, CellSide direction, double speed, int damage) {
    this(source, new Point((int) Math.round(col), (int) Math.round(row)), speed, damage);
    this.col = col;
    this.row = row;
    this.direction = direction;
    this.speed = speed;
    this.damage = damage;
    dxdy = CellSide.dxdy(direction);
  }
  
  public MazeObject getSource() {
    return source;
  }
  
  public double getSpeed() {
    return speed;
  }
  
  public int getDamage() {
    return damage;
  }
  
  public boolean doesSplashDamage() {
    return false;
  }
  
  public boolean isHoming() {
    return false;
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    if (direction != null) {
      return images.getImage(getType().name() + direction.name());
    }
    return images.getImage(getType().name());
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int offset) {
    int x = (int) (center_cell_corner.x - side_length * (ship_location.x - col));
    int y = (int) (center_cell_corner.y - side_length * (ship_location.y - row));
    g.drawImage(getImage(images), x + offset, y + offset, null);
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    if (isHoming()) {
      if (source.getType().equals(ObjectType.Pyro)) {
        trackRobot(maze, engine);
      }
      else {
        trackShip(maze, ships);
      }
    }
    int rounded_row = (int) Math.round(row);
    int rounded_col = (int) Math.round(col);
    if (!maze.isValidCell(rounded_col, rounded_row)) {
      is_destroyed = true;
      return null;
    }
    Point current = new Point(rounded_col, rounded_row);
    for (Pyro ship : ships) {
      if (Math.hypot(ship.getRow() - row, ship.getCol() - col) < 0.5 && !source.equals(ship)
              && !ship.isDestroyed()) {
        is_destroyed = true;
        ship.beDamaged(engine, damage, source, false);
        if (doesSplashDamage()) {
          doSplashDamage(engine, ship);
        }
        return new Explosion(col + 0.1 * dxdy.x, row + 0.1 * dxdy.y, num_transitions, Math.min(damage / 16.0,
                0.5));
      }
    }
    Iterator<Unit> it = engine.getRobots(current);
    if (it != null) {
      while (it.hasNext()) {
        Unit unit = it.next();
        if (unit != null && !source.equals(unit) && !unit.isDestroyed()) {
          if (Math.hypot(unit.getRow() - row, unit.getCol() - col) < 0.5) {
            is_destroyed = true;
            unit.beDamaged(engine, damage, source, false);
            if (doesSplashDamage()) {
              doSplashDamage(engine, unit);
            }
            return new Explosion(col + 0.1 * dxdy.x, row + 0.1 * dxdy.y, num_transitions, Math.min(
                    damage / 16.0, 0.5));
          }
        }
      }
    }
    int margin_row = (int) Math.round(row - 0.49 * dxdy.y);
    int margin_col = (int) Math.round(col - 0.49 * dxdy.x);
    if (maze.hasWall(margin_col, margin_row, direction)) {
      if (!maze.hasBarrier(margin_col, margin_row, direction)) {
        is_destroyed = true;
        if (doesSplashDamage()) {
          doSplashDamage(engine, null);
          return new Explosion(col + 0.1 * dxdy.x, row + 0.1 * dxdy.y, num_transitions, Math.min(
                  damage / 16.0, 0.5));
        }
        return null;
      }
    }
    row += (double) dxdy.y / num_transitions * speed;
    col += (double) dxdy.x / num_transitions * speed;
    location = new Point(rounded_col, rounded_row);
    return null;
  }
  
  public void doSplashDamage(MazeEngine engine, MazeObject direct_hit) {
    engine.doSplashDamage(col, row, damage, source, !source.getType().equals(ObjectType.Pyro), direct_hit);
  }
  
  public void trackShip(MazeUtility maze, ArrayList<Pyro> ships) {
    int rounded_row = (int) Math.round(row);
    int rounded_col = (int) Math.round(col);
    CellSide[] dirs = new CellSide[3];
    dirs[0] = direction;
    CellSide[] adjacents = CellSide.adjacents(direction);
    if (Math.random() < 0.5) {
      dirs[1] = adjacents[0];
      dirs[2] = adjacents[1];
    }
    else {
      dirs[1] = adjacents[1];
      dirs[2] = adjacents[0];
    }
    for (CellSide dir : dirs) {
      Pyro ship = acquireShipTarget(maze, ships, dir);
      if (ship != null) {
        if (!direction.equals(dir)) {
          direction = dir;
          dxdy = CellSide.dxdy(direction);
          col = rounded_col;
          row = rounded_row;
        }
        ship.missileLocked(col, row);
        break;
      }
    }
  }
  
  public Pyro acquireShipTarget(MazeUtility maze, ArrayList<Pyro> ships, CellSide dir) {
    int rounded_row = (int) Math.round(row);
    int rounded_col = (int) Math.round(col);
    Point current = new Point(rounded_col, rounded_row);
    while (maze.isValidCell(current.x, current.y)) {
      for (Pyro ship : ships) {
        if (!ship.isDestroyed() && !ship.isCloaked() && ship.getLocation().equals(current)) {
          return ship;
        }
      }
      if (maze.hasWall(current.x, current.y, dir) && !maze.hasBarrier(current.x, current.y, dir)) {
        break;
      }
      current = MazeWalker.getLocationInDirection(current.x, current.y, dir);
    }
    return null;
  }
  
  public void trackRobot(MazeUtility maze, MazeEngine engine) {
    int rounded_row = (int) Math.round(row);
    int rounded_col = (int) Math.round(col);
    if (engine.containsUncloakedRobot(location)) {
      Unit unit = engine.getVisibleRobot(location);
      direction = CellSide.bestDirection(col, row, unit.getCol(), unit.getRow(), direction);
      dxdy = CellSide.dxdy(direction);
      return;
    }
    if (!acquireRobotTarget(maze, engine, direction)) {
      CellSide[] dirs = CellSide.adjacents(direction);
      boolean left = acquireRobotTarget(maze, engine, dirs[0]);
      boolean right = acquireRobotTarget(maze, engine, dirs[1]);
      if (left && right) {
        direction = dirs[(int) (Math.random() * 2)];
        dxdy = CellSide.dxdy(direction);
        col = rounded_col;
        row = rounded_row;
      }
      else if (left) {
        direction = dirs[0];
        dxdy = CellSide.dxdy(direction);
        col = rounded_col;
        row = rounded_row;
      }
      else if (right) {
        direction = dirs[1];
        dxdy = CellSide.dxdy(direction);
        col = rounded_col;
        row = rounded_row;
      }
    }
  }
  
  public boolean acquireRobotTarget(MazeUtility maze, MazeEngine engine, CellSide dir) {
    int rounded_row = (int) Math.round(row);
    int rounded_col = (int) Math.round(col);
    Point current = new Point(rounded_col, rounded_row);
    Point next = MazeWalker.getLocationInDirection(current.x, current.y, dir);
    while (maze.isValidCell(next.x, next.y)) {
      if (maze.hasWall(current.x, current.y, dir)) {
        if (!maze.hasBarrier(current.x, current.y, dir)) {
          break;
        }
      }
      if (engine.containsUncloakedRobot(next)) {
        return true;
      }
      next = MazeWalker.getLocationInDirection(current.x, current.y, dir);
      current = new Point(next);
    }
    return false;
  }
}
