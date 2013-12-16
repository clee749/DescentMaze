package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.Shield;
import mazeobject.powerup.cannon.PlasmaCannon;

public class HeavyDriller extends Robot {
  public HeavyDriller(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new PlasmaCannon(3, 2.0, 7);
    action_reload = 4;
  }
  
  public HeavyDriller(Point location, CellSide direction) {
    this(location, direction, 29);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.HeavyDriller;
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
    if (rand < 0.30) {
      return new Shield(location);
    }
    if (rand < 0.40) {
      return new PlasmaCannon(location, 3.0, 4);
    }
    return null;
  }
  
  @Override
  public int getPoints() {
    return 1000;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot27.wav";
  }
}
