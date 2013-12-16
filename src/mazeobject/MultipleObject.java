package mazeobject;

import java.awt.Image;
import java.util.ArrayList;

import util.ImageHandler;

import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.unit.pyro.Pyro;

public class MultipleObject extends MazeObject {
  private final ArrayList<MazeObject> objects;
  
  public MultipleObject() {
    super(null);
    objects = new ArrayList<MazeObject>();
  }
  
  public ArrayList<MazeObject> getObjects() {
    return objects;
  }
  
  public void add(MazeObject object) {
    if (object != null) {
      objects.add(object);
    }
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    return null;
  }
  
  @Override
  public Image getImage(ImageHandler images) {
    return null;
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.MultipleObject;
  }
}
