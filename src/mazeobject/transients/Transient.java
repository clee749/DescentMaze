package mazeobject.transients;

import java.awt.Point;

import mazeobject.MazeObject;

public abstract class Transient extends MazeObject {
  protected int elapsed_time;
  protected int frames;
  
  public Transient(Point location, int frames) {
    super(location);
    this.frames = frames;
  }
}
