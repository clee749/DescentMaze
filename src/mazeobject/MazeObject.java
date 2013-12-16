package mazeobject;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.unit.pyro.Pyro;
import util.ImageHandler;

public abstract class MazeObject {
  protected Point location;
  protected CellSide direction;
  protected boolean is_destroyed;
  
  public MazeObject(Point location) {
    if (location != null) {
      this.location = new Point(location.x, location.y);
    }
  }
  
  public Point getLocation() {
    return location;
  }
  
  public void setLocation(Point location) {
    this.location = location;
  }
  
  public CellSide getDirection() {
    return direction;
  }
  
  public void setDirection(CellSide direction) {
    this.direction = direction;
  }
  
  public boolean isDestroyed() {
    return is_destroyed;
  }
  
  public void destroy() {
    is_destroyed = true;
  }
  
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int offset) {
    int x = center_cell_corner.x - side_length * (ship_location.x - location.x);
    int y = center_cell_corner.y - side_length * (ship_location.y - location.y);
    g.drawImage(getImage(images), x + offset, y + offset, null);
  }
  
  public abstract Image getImage(ImageHandler images);
  
  public abstract ObjectType getType();
  
  public abstract MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships);
  
  public abstract MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions);
}
