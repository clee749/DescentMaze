package mazeobject.unit.robot;

import java.awt.Point;

import maze.CellSide;
import mazeobject.ObjectType;

public class RobotFactory {
  protected RobotFactory() {
    
  }
  
  public static Robot newRobot(ObjectType type, Point location, CellSide direction) {
    switch (type) {
      case Green:
        return new Green(location, direction);
      case Yellow:
        return new Yellow(location, direction);
      case Red:
        return new Red(location, direction);
      case BabySpider:
        return new BabySpider(location, direction);
      case Class1Drone:
        return new Class1Drone(location, direction);
      case Class2Drone:
        return new Class2Drone(location, direction);
      case DefenseRobot:
        return new DefenseRobot(location, direction);
      case LightHulk:
        return new LightHulk(location, direction);
      case MediumHulk:
        return new MediumHulk(location, direction);
      case PlatformLaser:
        return new PlatformLaser(location, direction);
      case SecondaryLifter:
        return new SecondaryLifter(location, direction);
      case Spider:
        return new Spider(location, direction);
      case Bomber:
        return new Bomber(location, direction);
      case HeavyDriller:
        return new HeavyDriller(location, direction);
      case HeavyHulk:
        return new HeavyHulk(location, direction);
      case MediumHulkCloaked:
        return new MediumHulkCloaked(location, direction);
      case PlatformMissile:
        return new PlatformMissile(location, direction);
      default:
        return null;
    }
  }
  
  public static ObjectType[] lowThreats() {
    ObjectType[] robots = {ObjectType.Class1Drone, ObjectType.Class2Drone};
    return robots;
  }
  
  public static ObjectType[] mediumThreats() {
    ObjectType[] robots =
            {ObjectType.DefenseRobot, ObjectType.LightHulk, ObjectType.MediumHulk, ObjectType.PlatformLaser,
                    ObjectType.SecondaryLifter, ObjectType.Spider};
    return robots;
  }
  
  public static ObjectType[] highThreats() {
    ObjectType[] robots =
            {ObjectType.Bomber, ObjectType.HeavyDriller, ObjectType.HeavyHulk, ObjectType.MediumHulkCloaked,
                    ObjectType.PlatformMissile};
    return robots;
  }
  
  public static ObjectType[] epics() {
    ObjectType[] robots =
            {ObjectType.DefenseRobot, ObjectType.PlatformLaser, ObjectType.HeavyDriller,
                    ObjectType.HeavyHulk, ObjectType.PlatformMissile};
    return robots;
  }
  
  public static ObjectType[] generated() {
    ObjectType[] robots =
            {ObjectType.Class1Drone, ObjectType.Class2Drone, ObjectType.DefenseRobot, ObjectType.LightHulk,
                    ObjectType.MediumHulk, ObjectType.PlatformLaser, ObjectType.SecondaryLifter,
                    ObjectType.Spider, ObjectType.Bomber};
    return robots;
  }
}
