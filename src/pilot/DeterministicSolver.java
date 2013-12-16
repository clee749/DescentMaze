package pilot;

import java.awt.Point;

import maze.CellSide;
import maze.MazeUtility;

public class DeterministicSolver extends MazeWalker {
  private final CellSide[] order;
  
  public DeterministicSolver(MazeUtility maze, CellSide[] order) {
    super(maze);
    this.order = order;
  }
  
  @Override
  public CellSide nextUnvisited() {
    for (CellSide direction : order) {
      Point next = getLocationInDirection(location.x, location.y, direction);
      if (maze.isValidCell(next.x, next.y) && !maze.hasWall(location.x, location.y, direction)
              && !visited[next.x][next.y]) {
        path.push(direction);
        visited[next.x][next.y] = true;
        next_location = next;
        // System.out.println("Move " + direction.name() + " to " +
        // next);
        return direction;
      }
    }
    return null;
  }
}
