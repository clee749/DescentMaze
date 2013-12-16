package pilot;

import java.awt.Point;

import maze.CellSide;
import maze.MazeUtility;

public class RandomSolver extends MazeWalker {
  private final CellSide[] directions;
  private final Point[] next_locations;
  
  public RandomSolver(MazeUtility maze) {
    super(maze);
    directions = new CellSide[4];
    next_locations = new Point[4];
  }
  
  @Override
  public CellSide nextUnvisited() {
    int num_valid_directions = 0;
    for (CellSide direction : CellSide.values()) {
      Point next = getLocationInDirection(location.x, location.y, direction);
      if (maze.isValidCell(next.x, next.y) && !maze.hasWall(location.x, location.y, direction)
              && !visited[next.x][next.y]) {
        directions[num_valid_directions] = direction;
        next_locations[num_valid_directions] = next;
        ++num_valid_directions;
      }
    }
    if (num_valid_directions > 0) {
      int index = (int) (Math.random() * num_valid_directions);
      CellSide direction = directions[index];
      Point next = next_locations[index];
      path.push(direction);
      visited[next.x][next.y] = true;
      next_location = next_locations[index];
      return direction;
    }
    return null;
  }
}
