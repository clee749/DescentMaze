package maze;

/*
 * Author: Charles Lee MazeWallMaker.java This class makes the walls of a maze in two general
 * stages, by first creating a solution path and then filling in the unused cells. There is a large
 * amount of randomness involved, so it generates a different maze each time it is called.
 */
// package edu.brown.cs.cs019.maze;
// import edu.brown.cs.cs019.maze.utilities.CellSide;
// import edu.brown.cs.cs019.maze.utilities.MazeUtility;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class MazeWallMaker {
  private final int rows, cols;
  private final boolean[][] processed;
  private final MazeUtility maze;
  private Stack<Point> solutionPath;
  
  // private MazeGui gui;
  
  public MazeWallMaker(int rows, int cols, MazeUtility maze) {
    this.rows = rows;
    this.cols = cols;
    processed = new boolean[rows + 1][cols + 1];
    this.maze = maze;
    // gui = new MazeGui(maze, rows, cols);
  }
  
  /*
   * This method creates a path from the entrance to a cell on the bottom of the maze, which will
   * become the exit. The algorithm is as follows, beginning at the entrance: 1: Choose a random
   * number of cells to create before backtracking and a random number of cells to backtrack, which
   * is less than the first. 2: Choose a random valid direction to move in and connect the next cell
   * to the maze path. A valid direction points at a cell that is in the maze but not already on the
   * path. 3: If there are no valid directions from the current cell, unwind the stack of cells in
   * the solution path until there is a valid direction. 4: If the above determined maximum of moves
   * until a backtrack has been reached, unwind the solution path stack the number of times
   * specified by the number of moves to backtrack. 5: If the solution path stack is ever empty
   * (except in the beginning), start from the entrance and move down until a cell is found that has
   * not yet been added to the maze. 6: In general, repeat step 2 until the bottom of the maze is
   * reached, and that cell will be the exit. After a backtrack, repeat step 1. Parameter: entrance:
   * column number of entrance to maze Returns: column number of exit to maze
   */
  public int makeSolutionPath(int entrance) {
    maze.setEntrance(entrance);
    int col = entrance;
    int row = 0;
    int movesUntilBacktrack = (int) (Math.random() * 25);
    int numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
    solutionPath = new Stack<Point>();
    solutionPath.add(new Point(0, entrance));
    maze.makeBox(new Point(0, entrance));
    processed[0][entrance] = true;
    while (true) {
      ArrayList<Point> possibleMoves = findPossibleMoves(row, col);
      int size = possibleMoves.size();
      if (size == 0) {
        if (solutionPath.isEmpty()) {
          solutionPath.add(newSolutionPath(entrance));
          movesUntilBacktrack = (int) (Math.random() * 25);
          numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
          continue;
        }
        // System.out.println("popped");
        Point rejected = solutionPath.pop();
        row = rejected.x;
        col = rejected.y;
        continue;
      }
      if (movesUntilBacktrack == 0) {
        // System.out.println("backtracking");
        while (numMovesToBacktrack > 1) {
          if (solutionPath.isEmpty()) {
            solutionPath.add(newSolutionPath(entrance));
            movesUntilBacktrack = (int) (Math.random() * 25);
            numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
            continue;
          }
          solutionPath.pop();
          --numMovesToBacktrack;
        }
        if (solutionPath.isEmpty()) {
          solutionPath.add(newSolutionPath(entrance));
          movesUntilBacktrack = (int) (Math.random() * 25);
          numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
          continue;
        }
        Point lastBacktrack = solutionPath.pop();
        row = lastBacktrack.x;
        col = lastBacktrack.y;
        movesUntilBacktrack = (int) (Math.random() * 25);
        numMovesToBacktrack = (int) (Math.random() * movesUntilBacktrack);
        continue;
      }
      Point move = possibleMoves.get((int) (Math.random() * 10) % size);
      solutionPath.add(move);
      maze.makeBox(move);
      processed[move.x][move.y] = true;
      connectBox(row, col, move);
      row = move.x;
      col = move.y;
      --movesUntilBacktrack;
      if (row == rows - 1) {
        maze.setExit(col);
        return col;
      }
    }
  }
  
  /*
   * This method finds the beginning of a new solution path whenever there are no more directions in
   * which to move from the old path. It starts from the entrance and moves down until a cell is
   * found that has not yet been added to the maze. Parameter: entrance: column number of entrance
   * to maze Returns: first cell in new solution path
   */
  public Point newSolutionPath(int entrance) {
    // System.out.println("Hoping for an (empty stack) exception?");
    for (int col = entrance, row = 1;; ++row) {
      if (!processed[row][col]) {
        maze.makeBox(new Point(row, col));
        processed[row][col] = true;
        connectBox(row - 1, col, new Point(row, col));
        return new Point(row, col);
      }
    }
  }
  
  /*
   * This method finds the valid directions in which a new cell can be added to the maze.
   * Parameters: row: last added cell's row col: last added cell's column Returns: next cell to add
   * to path
   */
  public ArrayList<Point> findPossibleMoves(int row, int col) {
    ArrayList<Point> moves = new ArrayList<Point>();
    if (row > 0 && !processed[row - 1][col]) {
      moves.add(new Point(row - 1, col));
    }
    if (col > 0 && !processed[row][col - 1]) {
      moves.add(new Point(row, col - 1));
    }
    if (row < rows - 1 && !processed[row + 1][col]) {
      moves.add(new Point(row + 1, col));
    }
    if (col < cols - 1 && !processed[row][col + 1]) {
      moves.add(new Point(row, col + 1));
    }
    return moves;
  }
  
  /*
   * This method connects a cell to the path by tearing down the appropriate wall after a box has
   * been made around it. Parameters: row: row of last cell in path col: column of last cell in path
   * move: next cell in path Returns: nothing
   */
  public void connectBox(int row, int col, Point move) {
    if (move.x > row && move.y == col) {
      maze.setWall(row, col, CellSide.South, false);
    }
    else if (move.x < row && move.y == col) {
      maze.setWall(row, col, CellSide.North, false);
    }
    else if (move.y > col && move.x == row) {
      maze.setWall(row, col, CellSide.East, false);
    }
    else if (move.y < col && move.x == row) {
      maze.setWall(row, col, CellSide.West, false);
    }
    /*
     * gui.repaint(); try { Thread.sleep(500); } catch (InterruptedException e) {
     * e.printStackTrace(); }
     */
  }
  
  /*
   * This method finishes the maze after the solution path has been made by making a series of dead
   * ends connected to the solution path. The algorithm is as follows: 1: Cycle through the cells of
   * the maze until one is found that is not connected to the path. 2: Cycle through more cells
   * until one is found that is bordered by a cell on the path. Connect that cell to the path and
   * continue building the path until either a randomly determined maximum number of moves is
   * reached or there are no valid directions from the last added cell. 3: Repeat this procedure
   * until all of the cells are on the path. Parameters: inverse_frequency: inverse frequency of
   * safely removed locations saved Returns: nothing
   */
  public void completeMaze(int inverse_frequency) {
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < cols; ++col) {
        if (!processed[row][col]) {
          // System.out.println("filling");
          Point connectionCell = findConnectionCell(row, col);
          processed[connectionCell.x][connectionCell.y] = true;
          fillSpace(connectionCell, inverse_frequency);
          --col;
        }
      }
    }
  }
  
  /*
   * This method finds a cell that borders another cell on the path by checking the cells around it,
   * and if no good cells are found, recursing on another cell. When a good cell is found, it is
   * connected to the maze. Parameters: row: current cell's row col: current cell's column Returns:
   * newest cell on path
   */
  public Point findConnectionCell(int row, int col) {
    if (row > 0 && processed[row - 1][col]) {
      maze.makeBox(new Point(row, col));
      connectBox(row, col, new Point(row - 1, col));
      return new Point(row, col);
    }
    if (col > 0 && processed[row][col - 1]) {
      maze.makeBox(new Point(row, col));
      connectBox(row, col, new Point(row, col - 1));
      return new Point(row, col);
    }
    if (row < rows - 1 && processed[row + 1][col]) {
      maze.makeBox(new Point(row, col));
      connectBox(row, col, new Point(row + 1, col));
      return new Point(row, col);
    }
    if (col < cols - 1 && processed[row][col + 1]) {
      maze.makeBox(new Point(row, col));
      connectBox(row, col, new Point(row, col + 1));
      return new Point(row, col);
    }
    return findConnectionCell(row, col + 1);
  }
  
  /*
   * This method fills the unused space in the maze by building a dead end section extending from
   * the path. Parameters: connection: cell from which to build dead end on path Returns: nothing
   */
  public void fillSpace(Point connection, int inverse_frequency) {
    int row = connection.x;
    int col = connection.y;
    int maxMovesUntilDeadEnd = (int) (Math.random() * 50);
    while (maxMovesUntilDeadEnd > 0) {
      ArrayList<Point> possibleMoves = findPossibleMoves(row, col);
      int size = possibleMoves.size();
      if (size == 0) {
        return;
      }
      Point move = possibleMoves.get((int) (Math.random() * 10) % size);
      maze.makeBox(move);
      processed[row][col] = true;
      connectBox(row, col, move);
      row = move.x;
      col = move.y;
      --maxMovesUntilDeadEnd;
      if (Math.random() < 1.0 / inverse_frequency) {
        maze.addSafeBarrier(move);
      }
    }
  }
  
  public void setBarrier(int row, int col, boolean visible) {
    maze.makeBox(new Point(row, col));
    maze.putBarrier(row, col);
  }
}
