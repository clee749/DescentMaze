package mazeobject.unit.robot;

import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.MultipleObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.FireballCannon;
import mazeobject.unit.pyro.Pyro;
import mazeobject.weapon.Weapon;

public class Spider extends Robot {
  public Spider(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new FireballCannon(2, 1.0, 8);
    cannon_side = (int) (Math.random() * 3);
  }
  
  public Spider(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Spider;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Medium;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.04;
  }
  
  @Override
  public int getInverseSpeed() {
    return 3;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.30) {
      return new Shield(location);
    }
    return null;
  }
  
  @Override
  public Weapon fireCannon(MazeEngine engine) {
    double offset = ((cannon_side % 3) - 1) * getCannonOffset();
    Point dxdy = CellSide.dxdy(CellSide.next(direction));
    ++cannon_side;
    reload_left = cannon.getReload();
    engine.playSound(cannon.getShootingSound(false), location);
    return cannon.shoot(this, col - offset * dxdy.x, row - offset * dxdy.y, direction);
  }
  
  @Override
  public MazeObject finalAction(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    MultipleObject children = releaseChildren(maze);
    Powerup powerup = releasePowerup();
    if (children == null) {
      return powerup;
    }
    if (powerup != null) {
      children.add(powerup);
    }
    return children;
  }
  
  public MultipleObject releaseChildren(MazeUtility maze) {
    if (Math.random() < 0.1) {
      return null;
    }
    Point center = new Point((int) Math.round(col), (int) Math.round(row));
    ArrayList<Point> connected = maze.findConnectedCells(center, 1);
    connected.add(center);
    int count = (int) (Math.random() * connected.size() + 1);
    MultipleObject children = new MultipleObject();
    for (int i = 0; i < count; ++i) {
      int index = (int) (Math.random() * connected.size());
      children.add(new BabySpider(connected.get(index), direction));
      connected.remove(index);
    }
    return children;
  }
  
  @Override
  public int getPoints() {
    return 600;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot14.wav";
  }
}
