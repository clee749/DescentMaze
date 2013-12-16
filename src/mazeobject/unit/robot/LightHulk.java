package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.LaserCannon;

public class LightHulk extends Robot {
  public LightHulk(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new LaserCannon(1, 2.0, 3, 4);
    action_reload = 6;
  }
  
  public LightHulk(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.LightHulk;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Medium;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.25;
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
    if (rand < 0.40) {
      return new LaserCannon(location, 3.0, 5);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 500;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot04.wav";
  }
}
