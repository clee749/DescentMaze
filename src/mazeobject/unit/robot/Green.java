package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Energy;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.FireballCannon;

public class Green extends Robot {
  public Green(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new FireballCannon(1.0, 5);
  }
  
  public Green(Point location, CellSide direction) {
    this(location, direction, 9);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Green;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Low;
  }
  
  @Override
  public double getCannonOffset() {
    return 0;
  }
  
  @Override
  public int getInverseSpeed() {
    return 2;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.10) {
      return new Shield(location);
    }
    if (rand < 0.15) {
      return new Energy(location);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 400;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot13.wav";
  }
}
