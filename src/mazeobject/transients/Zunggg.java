package mazeobject.transients;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import util.ImageHandler;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;

public class Zunggg extends Transient {
  public Zunggg(Point location, int frames) {
    super(location, frames);
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return null;
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Zunggg;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    if (elapsed_time >= frames) {
      is_destroyed = true;
    }
    ++elapsed_time;
    return null;
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int unit_offset) {
    int half = side_length / 2;
    int x = center_cell_corner.x - side_length * (ship_location.x - location.x) + half;
    int y = center_cell_corner.y - side_length * (ship_location.y - location.y) + half;
    g.setColor(Color.cyan);
    g.setStroke(new BasicStroke(Math.min(side_length / 20, 2)));
    int num_bolts = Math.min(side_length / 5, 10);
    for (int i = 0; i < num_bolts; ++i) {
      g.drawLine(x, y, x + (int) (Math.random() * side_length - half), y
              + (int) (Math.random() * side_length - half));
    }
  }
}
