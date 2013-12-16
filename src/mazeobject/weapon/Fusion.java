package mazeobject.weapon;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.transients.Explosion;
import mazeobject.unit.Unit;
import mazeobject.unit.pyro.Pyro;

public class Fusion extends Weapon {
  private final HashSet<Unit> damaged;
  
  public Fusion(MazeObject source, double col, double row, CellSide direction, double speed, int damage) {
    super(source, col, row, direction, speed, damage);
    damaged = new HashSet<Unit>();
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Fusion;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    boolean hit = false;
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
        if (damaged.add(ship)) {
          ship.beDamaged(engine, damage, source, false);
          hit = true;
        }
      }
    }
    Iterator<Unit> it = engine.getRobots(current);
    if (it != null) {
      while (it.hasNext()) {
        Unit unit = it.next();
        if (unit != null && !source.equals(unit) && !unit.isDestroyed()) {
          if (Math.hypot(unit.getRow() - row, unit.getCol() - col) < 0.5) {
            if (damaged.add(unit)) {
              unit.beDamaged(engine, damage, source, false);
              hit = true;
            }
          }
        }
      }
    }
    int margin_row = (int) Math.round(row - 0.49 * dxdy.y);
    int margin_col = (int) Math.round(col - 0.49 * dxdy.x);
    if (maze.hasWall(margin_col, margin_row, direction)) {
      if (!maze.hasBarrier(margin_col, margin_row, direction)) {
        is_destroyed = true;
        return null;
      }
    }
    row += (double) dxdy.y / num_transitions * getSpeed();
    col += (double) dxdy.x / num_transitions * getSpeed();
    location = new Point(rounded_col, rounded_row);
    if (hit) {
      return new Explosion(col + 0.1 * dxdy.x, row + 0.1 * dxdy.y, num_transitions, Math.min(damage / 16.0,
              0.5));
    }
    return null;
  }
}
