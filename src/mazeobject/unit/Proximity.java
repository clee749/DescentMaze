package mazeobject.unit;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.transients.Explosion;
import mazeobject.unit.pyro.Pyro;
import util.ImageHandler;

public class Proximity extends Unit {
  private final Unit source;
  private final int damage;
  private int frame;
  private boolean fully_armed;
  
  public Proximity(Unit source, Point location, int damage) {
    super(location, null, 1);
    this.source = source;
    this.damage = damage;
    if (source.getType().equals(ObjectType.Pyro)) {
      fully_armed = false;
    }
    else {
      fully_armed = true;
    }
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Proximity;
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return images.getImage(getType().name(), frame / 2);
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    ++frame;
    if (!fully_armed && Math.hypot(source.getRow() - row, source.getCol() - col) > 0.5) {
      fully_armed = true;
    }
    if (!checkForCloseShips(ships)) {
      checkForCloseRobots(engine);
    }
    if (shields < 0) {
      if (!exploded) {
        exploded = true;
        engine.doSplashDamage(col, row, damage, source, false, null);
        return new Explosion(col, row, (int) (1.5 * num_transitions), 0.7);
      }
      --survival_left;
      if (survival_left <= 0) {
        is_destroyed = true;
      }
    }
    return null;
  }
  
  public boolean checkForCloseShips(ArrayList<Pyro> ships) {
    for (Pyro ship : ships) {
      if (Math.hypot(ship.getRow() - row, ship.getCol() - col) < 0.5 && !ship.isDestroyed()
              && (!source.equals(ship) || fully_armed)) {
        shields = -1;
        return true;
      }
    }
    return false;
  }
  
  public boolean checkForCloseRobots(MazeEngine engine) {
    Iterator<Unit> it = engine.getRobots(location);
    if (it != null) {
      while (it.hasNext()) {
        Unit unit = it.next();
        if (!source.equals(unit) && !unit.isDestroyed() && unit != this) {
          if (Math.hypot(unit.getRow() - row, unit.getCol() - col) < 0.5) {
            shields = -1;
            return true;
          }
        }
      }
    }
    return false;
  }
  
  @Override
  public void beDamaged(MazeEngine engine, int amount, MazeObject source, boolean is_splash) {
    if (!is_splash) {
      shields -= amount;
      if (shields < 0 && !exploded) {
        survival_left = MazeEngine.NUM_SHIFTS / 2;
      }
      engine.playSound("weapons/explode1.wav", location);
    }
  }
  
  @Override
  public MazeObject fireCannon(MazeEngine engine) {
    return null;
  }
  
  @Override
  public Powerup releasePowerup() {
    return null;
  }
}
