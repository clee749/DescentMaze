package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.LaserCannon;

public class PlatformLaser extends Robot {
  public PlatformLaser(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new LaserCannon(2, 1.9, 3, 3);
    action_reload = 3;
  }
  
  public PlatformLaser(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.PlatformLaser;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Medium;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.0;
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
  public int getPoints() {
    return 300;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot20.wav";
  }
}
