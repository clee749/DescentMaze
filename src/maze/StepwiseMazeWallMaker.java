package maze;

import java.awt.Point;
import java.util.ArrayList;

public class StepwiseMazeWallMaker {
  private final int cols;
  private final int rows;
  private final boolean[][] processed;
  private final MazeUtility maze;
  private final int maxMovesUntilHallEnd;
  private Point lastCell;
  private final boolean[][] onSolutionPath;
  private boolean solutionFound;
  private final ArrayList<Point> nonsurroundedCells;
  private int movesUntilHallEnd;
  
  public StepwiseMazeWallMaker(MazeUtility maze, int max_moves_until_hall_end) {
    cols = maze.getCols();
    rows = maze.getRows();
    processed = new boolean[cols][rows];
    this.maze = maze;
    this.maxMovesUntilHallEnd = max_moves_until_hall_end;
    onSolutionPath = new boolean[cols][rows];
    nonsurroundedCells = new ArrayList<Point>(cols * rows);
    movesUntilHallEnd = (int) (Math.random() * max_moves_until_hall_end);
    prepareMaze();
  }
  
  public boolean[][] getSolutionGrid() {
    return onSolutionPath;
  }
  
  public void prepareMaze() {
    for (int row = 0; row < rows; ++row) {
      maze.setWall(0, row, CellSide.West, true);
      maze.setWall(cols - 1, row, CellSide.East, true);
    }
    for (int col = 0; col < cols; ++col) {
      maze.setWall(col, 0, CellSide.North, true);
      maze.setWall(col, rows - 1, CellSide.South, true);
    }
  }
  
  public Point addCell(boolean fill_maze) {
    if (lastCell == null) {
      return addFirstCell();
    }
    if (!solutionFound || fill_maze) {
      return addGenericCell();
    }
    for (int col = 0; col < cols; ++col) {
      for (int row = 0; row < rows; ++row) {
        if (!onSolutionPath[col][row]) {
          maze.addSafeBarrier(new Point(col, row));
        }
      }
    }
    return null;
  }
  
  public Point addFirstCell() {
    int col = (int) (Math.random() * cols);
    Point cell = new Point(col, 0);
    processCell(cell);
    maze.setEntrance(col);
    maze.setWall(col, 0, CellSide.North, false);
    return cell;
  }
  
  public void processCell(Point cell) {
    maze.makeBox(cell);
    if (lastCell != null) {
      connectBox(lastCell.x, lastCell.y, cell);
    }
    processed[cell.x][cell.y] = true;
    lastCell = cell;
    if (!solutionFound) {
      onSolutionPath[cell.x][cell.y] = true;
      if (cell.y == rows - 1) {
        maze.setExit(lastCell.x);
        solutionFound = true;
        movesUntilHallEnd = 0;
        maze.setWall(cell.x, cell.y, CellSide.South, false);
        maze.setSolutionGrid(onSolutionPath);
        return;
      }
    }
    else {
      maze.addSafeBarrier(cell);
    }
    nonsurroundedCells.add(cell);
  }
  
  public Point addGenericCell() {
    if (movesUntilHallEnd < 1) {
      movesUntilHallEnd = (int) (Math.random() * maxMovesUntilHallEnd);
      return newHallwayPath();
    }
    ArrayList<Point> possibleMoves = findPossibleMoves(lastCell.x, lastCell.y);
    int size = possibleMoves.size();
    if (size == 0) {
      nonsurroundedCells.remove(lastCell);
      return newHallwayPath();
    }
    Point cell = possibleMoves.get((int) (Math.random() * size));
    processCell(cell);
    --movesUntilHallEnd;
    return cell;
  }
  
  public Point newHallwayPath() {
    while (!nonsurroundedCells.isEmpty()) {
      int index = (int) (Math.random() * nonsurroundedCells.size());
      lastCell = nonsurroundedCells.get(index);
      ArrayList<Point> possibleMoves = findPossibleMoves(lastCell.x, lastCell.y);
      int size = possibleMoves.size();
      if (size > 0) {
        Point cell = possibleMoves.get((int) (Math.random() * size));
        processCell(cell);
        movesUntilHallEnd = (int) (Math.random() * maxMovesUntilHallEnd);
        return cell;
      }
      nonsurroundedCells.remove(index);
    }
    return null;
  }
  
  public ArrayList<Point> findPossibleMoves(int col, int row) {
    ArrayList<Point> moves = new ArrayList<Point>();
    if (row > 0 && !processed[col][row - 1]) {
      moves.add(new Point(col, row - 1));
    }
    if (col > 0 && !processed[col - 1][row]) {
      moves.add(new Point(col - 1, row));
    }
    if (row < rows - 1 && !processed[col][row + 1]) {
      moves.add(new Point(col, row + 1));
    }
    if (col < cols - 1 && !processed[col + 1][row]) {
      moves.add(new Point(col + 1, row));
    }
    return moves;
  }
  
  public void connectBox(int col, int row, Point move) {
    if (move.x > col && move.y == row) {
      maze.setWall(col, row, CellSide.East, false);
    }
    else if (move.x < col && move.y == row) {
      maze.setWall(col, row, CellSide.West, false);
    }
    else if (move.x == col && move.y > row) {
      maze.setWall(col, row, CellSide.South, false);
    }
    else if (move.x == col && move.y < row) {
      maze.setWall(col, row, CellSide.North, false);
    }
  }
}
