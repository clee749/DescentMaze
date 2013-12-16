package mazeobject.powerup;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.unit.pyro.Pyro;
import util.ImageHandler;

public abstract class Powerup extends MazeObject {
  protected int frame;
  
  public Powerup(Point location) {
    super(location);
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return images.getImage(getType().name(), frame / 2);
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    for (Pyro ship : ships) {
      if (!ship.isDestroyed() && location.equals(ship.getLocation())) {
        if (ship.acquirePowerup(this)) {
          is_destroyed = true;
          if (ship.personalSoundsEnabled()) {
            engine.playSound(getAcquireSound());
          }
          break;
        }
      }
    }
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    ++frame;
    return null;
  }
  
  public String getAcquireSound() {
    return "effects/power03.wav";
  }
}
