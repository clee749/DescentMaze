package mazeobject.unit;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.Cannon;
import mazeobject.unit.pyro.Pyro;
import util.ImageHandler;

public abstract class Unit extends MazeObject {
  protected double col;
  protected double row;
  protected int shields;
  protected int reload_left;
  protected Cannon cannon;
  protected Point dxdy;
  protected Point next_location;
  protected int action_reload;
  protected int action_reload_left;
  protected boolean exploded;
  protected int survival_left;
  
  public Unit(Point location, CellSide direction, int shields) {
    super(location);
    col = location.x;
    row = location.y;
    this.direction = direction;
    this.shields = shields;
    dxdy = new Point(0, 0);
    next_location = null;
    action_reload_left = -1;
    setDirection(direction);
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return images.getImage(getType().name() + direction.name());
  }
  
  public int getShields() {
    return shields;
  }
  
  public double getCol() {
    return col;
  }
  
  public double getRow() {
    return row;
  }
  
  public Point getNextLocation() {
    return next_location;
  }
  
  public boolean isExploded() {
    return exploded;
  }
  
  @Override
  public void setLocation(Point location) {
    super.setLocation(location);
    col = location.x;
    row = location.y;
  }
  
  public boolean isVisible() {
    return true;
  }
  
  public boolean isCloaked() {
    return false;
  }
  
  public MazeObject finalAction(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return releasePowerup();
  }
  
  public abstract void beDamaged(MazeEngine engine, int amount, MazeObject source, boolean is_splash);
  
  public abstract MazeObject fireCannon(MazeEngine engine);
  
  public abstract Powerup releasePowerup();
}
