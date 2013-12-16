package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Energy;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.ConcussionCannon;

public class Yellow extends Robot {
  public Yellow(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new ConcussionCannon(2.0, 15);
  }
  
  public Yellow(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Yellow;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Medium;
  }
  
  @Override
  public double getCannonOffset() {
    return 0;
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
    if (rand < 0.35) {
      return new Energy(location);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 900;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot08.wav";
  }
}
