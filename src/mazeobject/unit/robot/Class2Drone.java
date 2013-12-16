package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.LaserCannon;

public class Class2Drone extends Robot {
  public Class2Drone(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new LaserCannon(1, 1.8, 3, 2);
  }
  
  public Class2Drone(Point location, CellSide direction) {
    this(location, direction, 9);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Class2Drone;
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
    if (rand < 0.30) {
      return new Shield(location);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 300;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot11.wav";
  }
}
