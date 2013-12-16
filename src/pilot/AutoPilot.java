package pilot;

import java.util.Enumeration;

import maze.CellSide;

public class AutoPilot implements PyroPilot {
  private Enumeration<CellSide> path;
  
  public AutoPilot(MazeWalker solver) {
    path = solver.getPathSequence();
  }
  
  @Override
  public CellSide makeNextMove() {
    if (path.hasMoreElements()) {
      return path.nextElement();
    }
    return null;
  }
}
