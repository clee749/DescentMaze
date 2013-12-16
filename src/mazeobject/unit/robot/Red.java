package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Energy;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.HomingCannon;

public class Red extends Robot {
  public Red(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new HomingCannon(0, 1.0, 5);
  }
  
  public Red(Point location, CellSide direction) {
    this(location, direction, 29);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Red;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.High;
  }
  
  @Override
  public double getCannonOffset() {
    return 0;
  }
  
  @Override
  public int getInverseSpeed() {
    return 4;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    if (rand < 0.40) {
      return new Shield(location);
    }
    if (rand < 0.45) {
      return new Energy(location);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 1600;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot10.wav";
  }
}
