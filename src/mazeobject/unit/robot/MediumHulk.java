package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.ConcussionMissile;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.ConcussionCannon;

public class MediumHulk extends Robot {
  public MediumHulk(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new ConcussionCannon(1.5, 16);
  }
  
  public MediumHulk(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.MediumHulk;
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
    return 3;
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
    return 500;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot03.wav";
  }
}
