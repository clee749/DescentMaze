package mazeobject.scenery;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import util.ImageHandler;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.unit.pyro.Pyro;

public abstract class Scenery extends MazeObject {
  public Scenery(Point location) {
    super(location);
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int offset) {
    super.paint(images, g, ship_location, center_cell_corner, side_length, 1);
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return images.getImage(getType().name());
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    return null;
  }
}
