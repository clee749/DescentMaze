package maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class StepwiseBacktrackingMazeWallMaker {
  private final int cols;
  private final int rows;
  private final boolean[][] processed;
  private final MazeUtility maze;
  private final int maxMovesUntilBacktrack;
  private final Stack<Point> currentPath;
  private final boolean[][] onSolutionPath;
  private boolean solutionFound;
  private final ArrayList<Point> nonsurroundedCells;
  private int movesUntilBacktrack;
  private int numMovesToBacktrack;
  
  public StepwiseBacktrackingMazeWallMaker(MazeUtility maze, int max_moves_until_backtrack) {
    cols = maze.getCols();
    rows = maze.getRows();
    processed = new boolean[cols][rows];
    this.maze = maze;
    this.maxMovesUntilBacktrack = max_moves_until_backtrack;
    currentPath = new Stack<Point>();
    onSolutionPath = new boolean[cols][rows];
    nonsurroundedCells = new ArrayList<Point>(cols * rows);
    movesUntilBacktrack = (int) (Math.random() * max_moves_until_backtrack);
    numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
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
    if (currentPath.isEmpty() && nonsurroundedCells.isEmpty()) {
      return addFirstCell();
    }
    if (!solutionFound) {
      return addSolutionCell();
    }
    if (fill_maze) {
      return addNonsolutionCell();
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
    maze.makeBox(cell);
    maze.setEntrance(col);
    postprocessCell(cell, true);
    maze.setWall(col, 0, CellSide.North, false);
    return cell;
  }
  
  public void postprocessCell(Point cell, boolean solution_cell) {
    processed[cell.x][cell.y] = true;
    currentPath.push(cell);
    if (solution_cell) {
      onSolutionPath[cell.x][cell.y] = true;
    }
    else {
      maze.addSafeBarrier(cell);
    }
    nonsurroundedCells.add(cell);
  }
  
  public Point addSolutionCell() {
    if (currentPath.isEmpty()) {
      Point cell = newSolutionPath(maze.getEntrance());
      movesUntilBacktrack = (int) (Math.random() * maxMovesUntilBacktrack);
      numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
      postprocessCell(cell, true);
      return cell;
    }
    Point lastCell = currentPath.peek();
    ArrayList<Point> possibleMoves = findPossibleMoves(lastCell.x, lastCell.y);
    int size = possibleMoves.size();
    if (size == 0) {
      nonsurroundedCells.remove(currentPath.pop());
      return addSolutionCell();
    }
    if (movesUntilBacktrack == 0) {
      while (numMovesToBacktrack > 1) {
        if (currentPath.isEmpty()) {
          Point cell = newSolutionPath(maze.getEntrance());
          movesUntilBacktrack = (int) (Math.random() * maxMovesUntilBacktrack);
          numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
          postprocessCell(cell, true);
          return cell;
        }
        currentPath.pop();
        --numMovesToBacktrack;
      }
      movesUntilBacktrack = (int) (Math.random() * maxMovesUntilBacktrack);
      numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
      return addSolutionCell();
    }
    Point cell = possibleMoves.get((int) (Math.random() * size));
    maze.makeBox(cell);
    connectBox(lastCell.x, lastCell.y, cell);
    --movesUntilBacktrack;
    postprocessCell(cell, true);
    if (cell.y == rows - 1) {
      maze.setExit(lastCell.x);
      solutionFound = true;
      currentPath.clear();
      nonsurroundedCells.remove(nonsurroundedCells.size() - 1);
      maze.setWall(cell.x, cell.y, CellSide.South, false);
      maze.setSolutionGrid(onSolutionPath);
    }
    return cell;
  }
  
  public Point newSolutionPath(int entrance) {
    for (int col = entrance, row = 1;; ++row) {
      if (!processed[col][row]) {
        Point cell = new Point(col, row);
        maze.makeBox(cell);
        connectBox(col, row - 1, cell);
        return cell;
      }
    }
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
  
  public Point addNonsolutionCell() {
    if (currentPath.isEmpty()) {
      int index = (int) (Math.random() * nonsurroundedCells.size());
      currentPath.push(nonsurroundedCells.get(index));
    }
    Point lastCell = currentPath.peek();
    ArrayList<Point> possibleMoves = findPossibleMoves(lastCell.x, lastCell.y);
    int size = possibleMoves.size();
    if (size == 0) {
      nonsurroundedCells.remove(currentPath.pop());
      if (nonsurroundedCells.isEmpty()) {
        return null;
      }
      return addNonsolutionCell();
    }
    if (movesUntilBacktrack == 0) {
      while (numMovesToBacktrack > 1) {
        if (currentPath.isEmpty()) {
          int index = (int) (Math.random() * nonsurroundedCells.size());
          currentPath.push(nonsurroundedCells.get(index));
        }
        currentPath.pop();
        --numMovesToBacktrack;
      }
      movesUntilBacktrack = (int) (Math.random() * maxMovesUntilBacktrack);
      numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
      return addNonsolutionCell();
    }
    Point cell = possibleMoves.get((int) (Math.random() * size));
    maze.makeBox(cell);
    connectBox(lastCell.x, lastCell.y, cell);
    --movesUntilBacktrack;
    postprocessCell(cell, false);
    return cell;
  }
}
