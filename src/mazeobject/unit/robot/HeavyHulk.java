package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.HomingMissile;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.HomingCannon;

public class HeavyHulk extends Robot {
  public HeavyHulk(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new HomingCannon(0, 1.0, 5);
  }
  
  public HeavyHulk(Point location, CellSide direction) {
    this(location, direction, 29);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.HeavyHulk;
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
    return 4;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.90) {
      return new HomingMissile(location);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 1500;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot07.wav";
  }
}
