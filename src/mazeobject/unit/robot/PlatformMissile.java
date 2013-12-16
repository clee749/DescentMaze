package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.ConcussionMissile;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.ConcussionCannon;

public class PlatformMissile extends Robot {
  public PlatformMissile(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new ConcussionCannon(3, 1.5, 16);
    action_reload = 4;
  }
  
  public PlatformMissile(Point location, CellSide direction) {
    this(location, direction, 29);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.PlatformMissile;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.High;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.0;
  }
  
  @Override
  public int getInverseSpeed() {
    return 4;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.90) {
      return new ConcussionMissile(location);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 2000;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot16.wav";
  }
}
