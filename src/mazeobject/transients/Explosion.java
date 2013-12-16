package mazeobject.transients;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.pyro.Pyro;
import util.ImageHandler;

public class Explosion extends Transient {
  private static Color[] colors = {Color.magenta, Color.orange, Color.pink, Color.red, Color.yellow,
          Color.white};
  
  private double radius;
  private final double max_radius;
  private double col;
  private double row;
  
  public Explosion(Point location, int frames, double max_radius) {
    super(location, frames);
    this.max_radius = max_radius;
    col = location.x;
    row = location.y;
  }
  
  public Explosion(double col, double row, int frames, double max_radius) {
    this(new Point((int) Math.round(col), (int) Math.round(row)), frames, max_radius);
    this.col = col;
    this.row = row;
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
    return ObjectType.Explosion;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    if (elapsed_time >= frames) {
      is_destroyed = true;
      return null;
    }
    int half = frames / 2;
    if (elapsed_time < half) {
      radius += 1.0 / frames;
    }
    else {
      radius -= 1.0 / frames;
    }
    ++elapsed_time;
    return null;
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int unit_offset) {
    int x = (int) (center_cell_corner.x - side_length * (ship_location.x - col) + side_length / 2);
    int y = (int) (center_cell_corner.y - side_length * (ship_location.y - row) + side_length / 2);
    int layer = 0;
    side_length *= max_radius;
    for (double r = radius; r > 0 && layer < 3; r -= 0.2) {
      // int margin = (int)(side_length / 2 - r * side_length / 2);
      g.setColor(colors[(int) (Math.random() * colors.length)]);
      g.fillOval((int) (x - r * side_length), (int) (y - r * side_length), (int) (2 * r * side_length),
              (int) (2 * r * side_length));
      ++layer;
    }
  }
}
