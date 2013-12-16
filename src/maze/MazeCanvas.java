package maze;

public interface MazeCanvas {
  public void setDisplayer(MazeDisplayer displayer);
  
  public void setEngine(MazeEngine engine);
  
  public void repaint();
}
