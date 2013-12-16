package maze.populator;

import java.awt.Point;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.ObjectType;
import mazeobject.powerup.Cloak;
import mazeobject.powerup.ConcussionPack;
import mazeobject.powerup.Energy;
import mazeobject.powerup.HomingPack;
import mazeobject.powerup.Invulnerability;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.ProximityPack;
import mazeobject.powerup.QuadLasers;
import mazeobject.powerup.Shield;
import mazeobject.powerup.SmartMissile;
import mazeobject.powerup.cannon.FusionCannon;
import mazeobject.powerup.cannon.LaserCannon;
import mazeobject.powerup.cannon.PlasmaCannon;
import mazeobject.scenery.Barrier;
import mazeobject.scenery.Entrance;
import mazeobject.scenery.Exit;
import mazeobject.scenery.Generator;
import mazeobject.scenery.Recharger;
import mazeobject.scenery.Scenery;
import mazeobject.unit.Unit;
import mazeobject.unit.robot.Green;
import mazeobject.unit.robot.Red;
import mazeobject.unit.robot.Yellow;

public class ClassicMazePopulator implements MazePopulator {
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
        if (safe_barriers[col][row] && Math.random() < 0.1) {
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
        ObjectType robot = ObjectType.Green;
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
    double high_prob = 2.0 * num_ships / 600; // 2n
    double medium_prob = 10.0 * num_ships / 600; // 10n
    double low_prob = (100.0 - 12 * num_ships) / 600; // 100 - 12n
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < cols; ++col) {
        if (row == 0 && col == maze.getEntrance()) {
          continue;
        }
        if (Math.random() < high_prob) {
          robots.add(new Red(new Point(col, row), CellSide.East));
        }
        else if (Math.random() < medium_prob) {
          robots.add(new Yellow(new Point(col, row), CellSide.East));
        }
        else if (Math.random() < low_prob) {
          robots.add(new Green(new Point(col, row), CellSide.East));
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
        if (Math.random() < 0.0167) { // 10 in 600 (1 / 60)
          powerups.add(new Shield(new Point(col, row)));
        }
        else if (Math.random() < 0.0167) { // 10 in 600 (1 / 60)
          powerups.add(new Energy(new Point(col, row)));
        }
      }
    }
    int row = (int) (Math.random() * rows);
    int col = (int) (Math.random() * cols);
    powerups.add(new QuadLasers(new Point(col, row)));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new LaserCannon(new Point(col, row), 3.0, 5));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new PlasmaCannon(new Point(col, row), 3.0, 4));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new FusionCannon(new Point(col, row), 3.0, 10));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new Cloak(new Point(col, row)));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new Invulnerability(new Point(col, row)));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new ConcussionPack(new Point(col, row)));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new HomingPack(new Point(col, row)));
    row = (int) (Math.random() * rows);
    col = (int) (Math.random() * cols);
    powerups.add(new ProximityPack(new Point(col, row)));
    for (int i = 0; i < 2; ++i) {
      row = (int) (Math.random() * rows);
      col = (int) (Math.random() * cols);
      powerups.add(new SmartMissile(new Point(col, row)));
    }
    return powerups;
  }
}
