package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.LaserCannon;

public class BabySpider extends Robot {
  public BabySpider(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new LaserCannon(1, 1.7, 3, 1);
  }
  
  public BabySpider(Point location, CellSide direction) {
    this(location, direction, 9);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.BabySpider;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Low;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.0;
  }
  
  @Override
  public int getInverseSpeed() {
    return 2;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    return null;
  }
  
  @Override
  public int getPoints() {
    return 100;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot12.wav";
  }
}
