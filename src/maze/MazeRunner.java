package maze;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import maze.populator.ClassicEpicMazePopulator;
import maze.populator.ClassicMazePopulator;
import maze.populator.EpicMazePopulator;
import maze.populator.MazePopulator;
import maze.populator.SparseMazePopulator;
import maze.populator.StandardMazePopulator;

/*
 * Author: Charles Lee MazeRunner.java This program generates a maze that has exactly 1 entrance, on
 * the top, and 1 exit, on the bottom. It is possible to reach any cell in the maze from the main
 * path, it is uniquely solvable, and a different maze is generated with each call. It begins by
 * making walls around the entire maze and selecting a random column for the entrance. Then it calls
 * the maze maker to create the body of the maze, states whether the maze is valid, and displays it.
 */
// package edu.brown.cs.cs019.maze;
// import edu.brown.cs.cs019.maze.utilities.CellSide;
// import edu.brown.cs.cs019.maze.utilities.MazeUtility;
// import edu.brown.cs.cs019.maze.utilities.MazeUtilityFactory;
public class MazeRunner {
  public final static int NUM_COLS = 30;
  public final static int NUM_ROWS = 20;
  public final static int NUM_SHIPS = 4;
  public final static long SLEEP = 35;
  
  private int cols, rows;
  private MazeUtility maze;
  private StepwiseBacktrackingMazeWallMaker maker;
  private final MazeDisplayer displayer;
  private final MazeEngine engine;
  
  public MazeRunner(MazeCanvas canvas) {
    displayer = new MazeDisplayer(canvas);
    engine = new MazeEngine(displayer);
    canvas.setDisplayer(displayer);
    canvas.setEngine(engine);
  }
  
  public int getCols() {
    return cols;
  }
  
  public int getRows() {
    return rows;
  }
  
  public MazeDisplayer getDisplayer() {
    return displayer;
  }
  
  public MazeEngine getEngine() {
    return engine;
  }
  
  public void newMaze(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;
    maze = MazeUtilityFactory.newMazeUtility(cols, rows);
    maker = new StepwiseBacktrackingMazeWallMaker(maze, 25);
  }
  
  public void createMaze(boolean epic, int num_ships) {
    initStepwiseMazeCreation();
    while (maker.addCell(!epic) != null) {
      displayer.drawMaze();
      try {
        Thread.sleep(SLEEP);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void initStepwiseMazeCreation() {
    displayer.setConstructionMaze(maze);
    displayer.setDisplayMode(true);
  }
  
  public boolean createMazeStep(boolean epic, int num_ships) {
    return maker.addCell(!epic) != null;
  }
  
  public void populateMaze(boolean epic, boolean classic, int num_ships) {
    MazePopulator populator;
    if (!classic && !epic) {
      populator = new StandardMazePopulator();
    }
    else if (!classic && epic) {
      if (Math.random() < 0.5) {
        populator = new EpicMazePopulator();
      }
      else {
        populator = new SparseMazePopulator();
      }
    }
    else if (classic && !epic) {
      populator = new ClassicMazePopulator();
    }
    else {
      populator = new ClassicEpicMazePopulator();
    }
    populator.populateMaze(maze, engine, num_ships);
  }
  
  /*
   * This method displays the maze if another program needs to use this class (hypothetically).
   */
  public void runMaze() {
    displayer.setDisplayMode(false);
    long time = System.currentTimeMillis();
    while (engine.displayNextFrame()) {
      try {
        Thread.sleep(Math.max(SLEEP - (System.currentTimeMillis() - time), 0));
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      time = System.currentTimeMillis();
    }
  }
  
  public static void main(String[] args) {
    MazePanel panel = new MazePanel();
    MazeRunner r = new MazeRunner(panel);
    
    JFrame frame = new JFrame();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(screen.width, screen.height - 50);
    frame.setTitle("MAP");
    frame.setVisible(true);
    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addComponentListener(panel);
    frame.addKeyListener(panel);
    frame.setMinimumSize(new Dimension(100, 100));
    
    for (int i = 1; i <= 15; ++i) {
      r.newMaze(NUM_COLS, NUM_ROWS);
      System.out.print(String.format("Mine MN%04d: ", panel.playMusic()));
      boolean epic = Math.random() < 0.2;
      int num_ships = (r.getDisplayer().nextLevelPlayable() ? 1 : NUM_SHIPS);
      r.createMaze(epic, num_ships);
      r.populateMaze(epic, Math.random() < 0.1, num_ships);
      r.runMaze();
      panel.stopMusic();
      System.out.println(String.format("Level %d complete!", i));
      try {
        Thread.sleep(2000);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    panel.closeMusic();
  }
}
