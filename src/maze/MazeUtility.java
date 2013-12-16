package maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import pilot.MazeWalker;

public class MazeUtility {
  private final int cols;
  private final int rows;
  private int entrance;
  private int exit;
  private final boolean[][] horizontal_walls;
  private final boolean[][] vertical_walls;
  private final boolean[][] safe_barriers;
  private final boolean[][] horizontal_barriers;
  private final boolean[][] vertical_barriers;
  private final boolean[][] barriers;
  private boolean[][] on_solution_path;
  
  public MazeUtility(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
    horizontal_walls = new boolean[rows + 1][cols];
    vertical_walls = new boolean[cols + 1][rows];
    safe_barriers = new boolean[cols][rows];
    horizontal_barriers = new boolean[rows + 1][cols];
    vertical_barriers = new boolean[cols + 1][rows];
    barriers = new boolean[cols][rows];
  }
  
  public int getCols() {
    return cols;
  }
  
  public int getRows() {
    return rows;
  }
  
  public int getEntrance() {
    return entrance;
  }
  
  public void setEntrance(int entrance) {
    this.entrance = entrance;
  }
  
  public int getExit() {
    return exit;
  }
  
  public void setExit(int exit) {
    this.exit = exit;
  }
  
  public boolean[][] getSafeBarriers() {
    return safe_barriers;
  }
  
  public boolean[][] getSolutionGrid() {
    return on_solution_path;
  }
  
  public void setSolutionGrid(boolean[][] on_solution_path) {
    this.on_solution_path = on_solution_path;
  }
  
  public boolean isValid() {
    
    return true;
  }
  
  public void setWall(int col, int row, CellSide side, boolean visible) {
    if (side.equals(CellSide.North)) {
      horizontal_walls[row][col] = visible;
    }
    else if (side.equals(CellSide.South)) {
      horizontal_walls[row + 1][col] = visible;
    }
    else if (side.equals(CellSide.West)) {
      vertical_walls[col][row] = visible;
    }
    else {
      vertical_walls[col + 1][row] = visible;
    }
  }
  
  public void makeBox(Point cell) {
    setWall(cell.x, cell.y, CellSide.East, true);
    setWall(cell.x, cell.y, CellSide.North, true);
    setWall(cell.x, cell.y, CellSide.West, true);
    setWall(cell.x, cell.y, CellSide.South, true);
  }
  
  public boolean hasWall(int col, int row, CellSide direction) {
    if (direction.equals(CellSide.North)) {
      return horizontal_walls[row][col];
    }
    if (direction.equals(CellSide.South)) {
      return horizontal_walls[row + 1][col];
    }
    if (direction.equals(CellSide.West)) {
      return vertical_walls[col][row];
    }
    return vertical_walls[col + 1][row];
  }
  
  public boolean hasWall(Point cell, CellSide direction) {
    if (direction.equals(CellSide.North)) {
      return horizontal_walls[cell.y][cell.x];
    }
    if (direction.equals(CellSide.South)) {
      return horizontal_walls[cell.y + 1][cell.x];
    }
    if (direction.equals(CellSide.West)) {
      return vertical_walls[cell.x][cell.y];
    }
    return vertical_walls[cell.x + 1][cell.y];
  }
  
  public boolean isValidCell(int col, int row) {
    if (0 <= row && row < rows && 0 <= col && col < cols) {
      return true;
    }
    return false;
  }
  
  public void addSafeBarrier(Point location) {
    safe_barriers[location.x][location.y] = true;
  }
  
  public ArrayList<Point> findConnectedCells(Point cell, int max_distance) {
    ArrayList<Point> connected = new ArrayList<Point>();
    connected.addAll(connectedCells(cell, max_distance, 0, null));
    return connected;
  }
  
  public LinkedList<Point> connectedCells(Point cell, int max_distance, int current_distance, CellSide from) {
    LinkedList<Point> connected = new LinkedList<Point>();
    if (current_distance < max_distance) {
      CellSide ignore = CellSide.opposite(from);
      for (CellSide direction : CellSide.values()) {
        if (direction.equals(ignore)) {
          continue;
        }
        Point next = MazeWalker.getLocationInDirection(cell.x, cell.y, direction);
        if (isValidCell(next.x, next.y) && !hasWall(cell.x, cell.y, direction)) {
          connected.add(next);
          connected.addAll(connectedCells(next, max_distance, current_distance + 1, direction));
        }
      }
    }
    return connected;
  }
  
  public void putBarrier(int col, int row) {
    horizontal_barriers[row][col] = true;
    horizontal_barriers[row + 1][col] = true;
    vertical_barriers[col][row] = true;
    vertical_barriers[col + 1][row] = true;
    barriers[col][row] = true;
  }
  
  public boolean hasBarrier(int col, int row, CellSide direction) {
    if (direction.equals(CellSide.North)) {
      return horizontal_barriers[row][col];
    }
    if (direction.equals(CellSide.South)) {
      return horizontal_barriers[row + 1][col];
    }
    if (direction.equals(CellSide.West)) {
      return vertical_barriers[col][row];
    }
    return vertical_barriers[col + 1][row];
  }
  
  public boolean hasBarrier(int col, int row) {
    return barriers[col][row];
  }
}
