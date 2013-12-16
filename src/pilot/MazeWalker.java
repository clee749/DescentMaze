package pilot;

import java.awt.Point;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Stack;

import maze.CellSide;
import maze.MazeUtility;

public abstract class MazeWalker implements PyroPilot {
  protected MazeUtility maze;
  protected int exit;
  protected int rows;
  protected Point location;
  protected Point next_location;
  protected boolean[][] visited;
  protected Stack<CellSide> path;
  
  public MazeWalker(MazeUtility maze) {
    this.maze = maze;
    rows = maze.getRows();
    exit = maze.getExit();
    location = new Point(maze.getEntrance(), 0);
    next_location = new Point(location);
    visited = new boolean[maze.getCols()][maze.getRows()];
    visited[location.x][0] = true;
    path = new Stack<CellSide>();
  }
  
  public Enumeration<CellSide> getPathSequence() {
    return path.elements();
  }
  
  @Override
  public CellSide makeNextMove() {
    location = next_location;
    if (location.x == exit && location.y == rows - 1) {
      return null;
    }
    CellSide direction = nextUnvisited();
    if (direction == null) {
      direction = CellSide.opposite(path.pop());
      next_location = getLocationInDirection(location.x, location.y, direction);
      // System.out.println("Move " + direction.name() + " to " + next +
      // " (revisiting)");
    }
    return direction;
  }
  
  public boolean atExit() {
    if (location.y == rows - 1 && location.x == exit) {
      return true;
    }
    return false;
  }
  
  public static Point getLocationInDirection(int col, int row, CellSide direction) {
    if (direction.equals(CellSide.North)) {
      return new Point(col, row - 1);
    }
    if (direction.equals(CellSide.South)) {
      return new Point(col, row + 1);
    }
    if (direction.equals(CellSide.West)) {
      return new Point(col - 1, row);
    }
    return new Point(col + 1, row);
  }
  
  public static Point getLocationInDirection(Point cell, CellSide direction) {
    if (direction.equals(CellSide.North)) {
      return new Point(cell.x, cell.y - 1);
    }
    if (direction.equals(CellSide.South)) {
      return new Point(cell.x, cell.y + 1);
    }
    if (direction.equals(CellSide.West)) {
      return new Point(cell.x - 1, cell.y);
    }
    return new Point(cell.x + 1, cell.y);
  }
  
  public static LinkedList<CellSide> findPath(MazeUtility maze, boolean[][] revealed, Point current,
          Point target) {
    return MazeWalker.findPath(maze, revealed, current, target, null, 1);
  }
  
  public static LinkedList<CellSide> findPath(MazeUtility maze, boolean[][] revealed, Point current,
          Point target, CellSide ignore_dir, int num_unrevealed_allowed) {
    if (current.equals(target)) {
      return new LinkedList<CellSide>();
    }
    for (CellSide direction : CellSide.values()) {
      if (direction.equals(ignore_dir)) {
        continue;
      }
      if (!maze.hasWall(current, direction)) {
        Point next = MazeWalker.getLocationInDirection(current, direction);
        if (maze.isValidCell(next.x, next.y)) {
          LinkedList<CellSide> path = null;
          if (!revealed[next.x][next.y]) {
            if (num_unrevealed_allowed > 0) {
              path =
                      MazeWalker.findPath(maze, revealed, next, target, CellSide.opposite(direction),
                              num_unrevealed_allowed - 1);
            }
          }
          else {
            path =
                    MazeWalker.findPath(maze, revealed, next, target, CellSide.opposite(direction),
                            num_unrevealed_allowed);
          }
          if (path != null) {
            path.addFirst(direction);
            return path;
          }
        }
      }
    }
    return null;
  }
  
  public abstract CellSide nextUnvisited();
}
