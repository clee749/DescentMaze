// package org.jdesktop.jdic.screensaver.bouncingline;

import java.awt.Component;
import java.awt.Graphics;
import java.util.Calendar;

import maze.MazeCanvas;
import maze.MazeDisplayer;
import maze.MazeEngine;
import maze.MazeRunner;

import org.jdesktop.jdic.screensaver.ScreensaverSettings;
import org.jdesktop.jdic.screensaver.SimpleScreensaver;

public class MazeScreensaver extends SimpleScreensaver implements MazeCanvas {
  public final static int NUM_COLS = 30;
  public final static int NUM_ROWS = 20;
  public static final int SIGHT_RADIUS = 5;
  public static final int NUM_SHIPS = 4;
  public final static long SLEEP = 30;
  
  private MazeRunner runner;
  private MazeEngine engine;
  private MazeDisplayer displayer;
  private boolean constructing_maze;
  private boolean epic;
  private boolean classic;
  private long last_update_time;
  private long sleep;
  
  private boolean always_epic;
  
  /**
   * Initialize this screen saver.
   */
  @Override
  public void init() {
    ScreensaverSettings settings = getContext().getSettings();
    always_epic = !(settings.getProperty("epic") == null);
    runner = new MazeRunner(this);
    newMaze();
    displayer.readImages();
    engine.resetScoreOnRespawn(true);
    sleep = SLEEP;
    last_update_time = System.currentTimeMillis();
  }
  
  /**
   * Paint the next frame.
   */
  @Override
  public void paint(Graphics g) {
    long time = System.currentTimeMillis();
    if (time - last_update_time < sleep) {
      return;
    }
    last_update_time = time;
    if (constructing_maze) {
      paintMazeConstruction(g);
    }
    else {
      paintMazeTraversal(g);
    }
  }
  
  public void paintMazeConstruction(Graphics g) {
    boolean still_constructing = runner.createMazeStep(epic, NUM_SHIPS);
    displayer.paint(g);
    if (!still_constructing) {
      constructing_maze = false;
      displayer.setDisplayMode(false);
    }
    if (!constructing_maze) {
      runner.populateMaze(epic, classic, NUM_SHIPS);
    }
    sleep = SLEEP;
  }
  
  public void paintMazeTraversal(Graphics g) {
    boolean more_frames = engine.displayNextFrame();
    displayer.paint(g);
    if (more_frames) {
      sleep = SLEEP;
    }
    else {
      newMaze();
      sleep = 1000;
    }
  }
  
  @Override
  public void setDisplayer(MazeDisplayer displayer) {
    this.displayer = displayer;
  }
  
  @Override
  public void setEngine(MazeEngine engine) {
    this.engine = engine;
  }
  
  @Override
  public void repaint() {
    // empty method
  }
  
  public void newMaze() {
    runner.newMaze(NUM_COLS, NUM_ROWS);
    runner.initStepwiseMazeCreation();
    constructing_maze = true;
    Component c = getContext().getComponent();
    displayer.setSizes(c.getSize());
    Calendar calendar = Calendar.getInstance();
    epic = always_epic || Math.random() < 0.1;
    if (calendar.get(Calendar.MONTH) == 3 && calendar.get(Calendar.DATE) == 1) {
      classic = Math.random() < 0.5;
      if (!engine.soundsActive()) {
        engine.toggleSounds();
      }
    }
    else {
      classic = Math.random() < 0.01;
    }
  }
}
