package maze.populator;

import maze.MazeEngine;
import maze.MazeUtility;

public interface MazePopulator {
  public void populateMaze(MazeUtility maze, MazeEngine engine, int num_ships);
}
