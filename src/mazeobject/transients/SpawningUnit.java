package mazeobject.transients;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import util.ImageHandler;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.unit.Unit;
import mazeobject.unit.pyro.Pyro;

public class SpawningUnit extends Transient {
  private final Zunggg zunggg;
  private final Unit unit;
  
  public SpawningUnit(Point location, int num_shifts, Unit unit) {
    super(location, num_shifts);
    zunggg = new Zunggg(location, num_shifts);
    this.unit = unit;
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
    return ObjectType.SpawningUnit;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    ++elapsed_time;
    if (elapsed_time == 1) {
      return zunggg;
    }
    if (elapsed_time == frames - 1) {
      return unit;
    }
    if (elapsed_time >= frames) {
      is_destroyed = true;
    }
    return null;
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int unit_offset) {
    return;
  }
}
