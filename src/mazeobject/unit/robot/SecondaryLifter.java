package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import maze.MazeEngine;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.cannon.LaserCannon;
import mazeobject.weapon.Weapon;

public class SecondaryLifter extends Robot {
  public SecondaryLifter(Point location, CellSide direction, int shields) {
    super(location, direction, shields);
    cannon = new LaserCannon(1, 1.7, 3, 1);
    action_reload = 6;
    cannon_side = 0;
  }
  
  public SecondaryLifter(Point location, CellSide direction) {
    this(location, direction, 19);
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.SecondaryLifter;
  }
  
  @Override
  public ThreatLevel getThreat() {
    return ThreatLevel.Medium;
  }
  
  @Override
  public double getCannonOffset() {
    return 0.15;
  }
  
  @Override
  public int getInverseSpeed() {
    return 2;
  }
  
  @Override
  public Powerup getReleasedPowerup(Point location, double rand) {
    return null;
  }
  
  @Override
  public Weapon fireCannon(MazeEngine engine) {
    Weapon shot = super.fireCannon(engine);
    ++cannon_side;
    return shot;
  }
  
  @Override
  public int getPoints() {
    return 400;
  }
  
  @Override
  public String getGrowlSound() {
    return "enemies/robot25.wav";
  }
}
