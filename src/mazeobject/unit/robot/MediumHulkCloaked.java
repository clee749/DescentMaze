package mazeobject.unit.robot;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Cloak;
import mazeobject.powerup.ConcussionMissile;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.ConcussionCannon;
import mazeobject.unit.pyro.Pyro;
import mazeobject.weapon.Weapon;
import util.ImageHandler;

public class MediumHulkCloaked extends Robot {
  private boolean visible;
  private boolean revealed;
  
  public MediumHulkCloaked(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new ConcussionCannon(1.5, 16);
  }
  
  public MediumHulkCloaked(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.MediumHulkCloaked;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.High;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.25;
  }
  
  @Override
  public int getInverseSpeed() {
    return 3;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.80) {
      return new Cloak(location);
    }
    if (rand < 0.90) {
      return new ConcussionMissile(location);
    }
    return null;
  }
  
  @Override
  public boolean isVisible() {
    return visible;
  }
  
  @Override
  public boolean isCloaked() {
    return true;
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int offset) {
    if (visible) {
      super.paint(images, g, ship_location, center_cell_corner, side_length, offset);
    }
  }
  
  @Override
  public void beDamaged(MazeEngine engine, int amount, MazeObject source, boolean is_splash) {
    super.beDamaged(engine, amount, source, is_splash);
    visible = true;
    revealed = true;
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    revealed = false;
    return super.act(maze, engine, ships);
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    visible = revealed;
    return super.transitionAct(maze, engine, ships, num_transitions);
  }
  
  @Override
  public Weapon fireCannon(MazeEngine engine) {
    revealed = true;
    return super.fireCannon(engine);
  }
  
  @Override
  public int getPoints() {
    return 700;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot03.wav";
  }
}
