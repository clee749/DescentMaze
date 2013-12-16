package maze.populator;

import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.ObjectType;
import mazeobject.powerup.Invulnerability;
import mazeobject.powerup.Powerup;
import mazeobject.scenery.Barrier;
import mazeobject.scenery.Entrance;
import mazeobject.scenery.Exit;
import mazeobject.scenery.Generator;
import mazeobject.scenery.Recharger;
import mazeobject.scenery.Scenery;
import mazeobject.unit.Unit;
import mazeobject.unit.robot.RobotFactory;

public class EpicMazePopulator implements MazePopulator {
  @Override
  public void populateMaze(MazeUtility maze, MazeEngine engine, int num_ships) {
    engine.setInitials(maze, placeScenery(maze, num_ships), placeRobots(maze, num_ships),
            placePowerups(maze), num_ships);
  }
  
  public ArrayList<Scenery> placeScenery(MazeUtility maze, int num_ships) {
    int cols = maze.getCols();
    int rows = maze.getRows();
    ArrayList<Scenery> scenery = new ArrayList<Scenery>();
    Point start = new Point(maze.getEntrance(), 0);
    Point end = new Point(maze.getExit(), rows - 1);
    scenery.add(new Entrance(start));
    scenery.add(new Exit(end));
    boolean[][] safe_barriers = maze.getSafeBarriers();
    for (int col = 0; col < cols; ++col) {
      for (int row = 0; row < rows; ++row) {
        if (safe_barriers[col][row]) {
          Point cell = new Point(col, row);
          maze.putBarrier(col, row);
          scenery.add(new Barrier(cell));
          maze.makeBox(cell);
        }
      }
    }
    CellSide[] dirs = CellSide.values();
    for (int i = 0; i < rows * cols / 600; ++i) {
      Point middle;
      boolean found;
      do {
        middle = new Point((int) (Math.random() * cols), (int) (Math.random() * rows));
        found = true;
        for (Scenery current : scenery) {
          if (current.getLocation().equals(middle)) {
            found = false;
            break;
          }
        }
      } while (!found);
      ArrayList<Point> rechargers = maze.findConnectedCells(middle, 2);
      scenery.add(new Recharger(middle));
      for (Point cell : rechargers) {
        if (!cell.equals(start) && !cell.equals(end)) {
          scenery.add(new Recharger(cell));
        }
      }
      for (int count = 0; count < num_ships; ++count) {
        ObjectType[] epics = RobotFactory.epics();
        ObjectType robot = epics[(int) (Math.random() * epics.length)];
        do {
          middle = new Point((int) (Math.random() * cols), (int) (Math.random() * rows));
          found = true;
          for (Scenery current : scenery) {
            if (current.getLocation().equals(middle)) {
              found = false;
              break;
            }
          }
        } while (!found);
        scenery.add(new Generator(middle, maze, robot, dirs[(int) (Math.random() * 4)], num_ships > 2));
      }
    }
    return scenery;
  }
  
  public ArrayList<Unit> placeRobots(MazeUtility maze, int num_ships) {
    int cols = maze.getCols();
    int rows = maze.getRows();
    ArrayList<Unit> robots = new ArrayList<Unit>();
    CellSide[] dirs = CellSide.values();
    ObjectType[] epics = RobotFactory.epics();
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < cols; ++col) {
        if (row == 0 && col == maze.getEntrance()) {
          continue;
        }
        if (Math.random() < 0.167) { // 100 in 600 (1 / 6)
          int rand = (int) (Math.random() * epics.length);
          robots.add(RobotFactory.newRobot(epics[rand], new Point(col, row), dirs[(int) (Math.random() * 4)]));
        }
      }
    }
    return robots;
  }
  
  public ArrayList<Powerup> placePowerups(MazeUtility maze) {
    int cols = maze.getCols();
    int rows = maze.getRows();
    ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < cols; ++col) {
        if (Math.random() < 0.0333) { // 20 in 600 (1 / 30)
          powerups.add(new Invulnerability(new Point(col, row)));
        }
      }
    }
    return powerups;
  }
}
