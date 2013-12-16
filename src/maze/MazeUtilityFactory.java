package maze;

public class MazeUtilityFactory {
  protected MazeUtilityFactory() {
    
  }
  
  public static MazeUtility newMazeUtility(int cols, int rows) {
    return new MazeUtility(cols, rows);
  }
}
