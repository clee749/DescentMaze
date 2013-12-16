package mazeobject;

public enum ObjectType {
  // Descent ship
  Pyro,
  
  // scenery
  Barrier, Recharger, Generator, Entrance, Exit,
  
  // robots
  Green, Yellow, Red, BabySpider, Class1Drone, Class2Drone, DefenseRobot, LightHulk, MediumHulk, PlatformLaser, SecondaryLifter, Spider, Bomber, HeavyDriller, HeavyHulk, MediumHulkCloaked, PlatformMissile,
  
  // weapons
  Laser, Plasma, Fusion, Fireball, Concussion, Homing, Smart, SmartPlasma,
  
  // power ups
  Shield, Energy, QuadLasers, Cloak, Invulnerability, ConcussionMissile, ConcussionPack, HomingMissile, HomingPack, ProximityPack, SmartMissile, LaserCannon, PlasmaCannon, FusionCannon, FireballCannon, ConcussionCannon, HomingCannon,
  
  // transients
  Explosion, Zunggg, SpawningUnit,
  
  // miscellaneous
  Proximity, MultipleObject;
  
  public static ObjectType[] getScenery() {
    ObjectType[] scenery = {Barrier, Recharger, Generator, Entrance, Exit};
    return scenery;
  }
  
  public static ObjectType[] getRobots() {
    ObjectType[] robots =
            {Green, Yellow, Red, BabySpider, Class1Drone, Class2Drone, DefenseRobot, LightHulk, MediumHulk,
                    PlatformLaser, SecondaryLifter, Spider, Bomber, HeavyDriller, HeavyHulk,
                    MediumHulkCloaked, PlatformMissile, Proximity};
    return robots;
  }
  
  public static ObjectType[] getWeapons() {
    ObjectType[] weapons = {Laser, Plasma, Fusion, Fireball, Concussion, Homing, Smart, SmartPlasma};
    return weapons;
  }
  
  public static ObjectType[] getPowerups() {
    ObjectType[] powerups =
            {Shield, Energy, QuadLasers, Cloak, Invulnerability, ConcussionMissile, ConcussionPack,
                    HomingMissile, HomingPack, ProximityPack, SmartMissile, LaserCannon, PlasmaCannon,
                    FusionCannon, FireballCannon, ConcussionCannon, HomingCannon};
    return powerups;
  }
}
