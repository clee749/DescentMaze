package maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import mazeobject.MazeObject;
import mazeobject.powerup.Powerup;
import mazeobject.scenery.Scenery;
import mazeobject.transients.Transient;
import mazeobject.unit.Unit;
import mazeobject.unit.pyro.ClickToMovePyro;
import mazeobject.unit.pyro.Pyro;
import mazeobject.weapon.Weapon;
import pilot.MazeWalker;
import util.ImageHandler;

public class MazeDisplayer {
  public static int SIGHT_RADIUS = 5;
  
  private final MazeCanvas canvas;
  private MazeUtility maze;
  private final ImageHandler images;
  private Font descent_font;
  private final MazeConstructionDisplayer construction_displayer;
  private boolean constructing_maze;
  private boolean[][] revealed;
  private int cols;
  private int rows;
  private int cell_side_length;
  private int unit_offset;
  private int powerup_offset;
  private int actual_sight_radius;
  private int extra_cols;
  private Point center;
  private Dimension dim;
  private ArrayList<Scenery> scenery;
  private ArrayList<Unit> robots;
  private ArrayList<Powerup> powerups;
  private ArrayList<Weapon> shots;
  private ArrayList<Transient> transients;
  private ArrayList<Pyro> ships;
  private Pyro center_ship;
  private Image buffer;
  private Graphics2D bufferg;
  private boolean next_level_playable;
  private boolean playable;
  
  public MazeDisplayer(MazeCanvas canvas) {
    this.canvas = canvas;
    images = new ImageHandler();
    construction_displayer = new MazeConstructionDisplayer();
    readFonts();
  }
  
  public int getSightRadius() {
    return actual_sight_radius;
  }
  
  public void readFonts() {
    InputStream is = MazeDisplayer.class.getResourceAsStream("/fonts/DescScor.TTF");
    try {
      descent_font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(12.0f);
    }
    catch (FontFormatException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void setInitials(MazeUtility maze, int cols, int rows, ArrayList<Scenery> scenery,
          ArrayList<Unit> robots, ArrayList<Powerup> powerups, ArrayList<Weapon> shots,
          ArrayList<Transient> transients, ArrayList<Pyro> ships, Pyro center_ship) {
    this.maze = maze;
    this.cols = cols;
    this.rows = rows;
    this.scenery = scenery;
    this.robots = robots;
    this.powerups = powerups;
    this.shots = shots;
    this.transients = transients;
    this.ships = ships;
    this.center_ship = center_ship;
    revealed = new boolean[cols][rows];
    playable = next_level_playable;
  }
  
  public void setSizes(Dimension new_size) {
    dim = new Dimension(new_size);
    cell_side_length =
            ((dim.height - 2 * SIGHT_RADIUS) / (2 * SIGHT_RADIUS + 1) / MazeEngine.NUM_SHIFTS + 1)
                    * MazeEngine.NUM_SHIFTS;
    unit_offset = (int) (cell_side_length * 0.2);
    powerup_offset = (int) (cell_side_length * 0.3);
    actual_sight_radius = dim.height / 2 / cell_side_length + 1;
    extra_cols = (dim.width - dim.height) / (2 * cell_side_length) + 2;
    center = new Point((dim.width - cell_side_length) / 2, (dim.height - cell_side_length) / 2);
    buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
    bufferg = (Graphics2D) buffer.getGraphics();
    construction_displayer.setSizes(dim);
  }
  
  public void readImages() {
    System.out.print("Reading images ... ");
    images.readImages("/images", cell_side_length - 2, cell_side_length - 2 * unit_offset, cell_side_length
            - 2 * unit_offset, cell_side_length - 2 * powerup_offset);
    System.out.println("Done!");
  }
  
  public void setCenterShip(Pyro center_ship) {
    this.center_ship = center_ship;
  }
  
  public void setDisplayMode(boolean constructing_maze) {
    this.constructing_maze = constructing_maze;
  }
  
  public void setConstructionMaze(MazeUtility maze) {
    construction_displayer.setMaze(maze);
  }
  
  public boolean nextLevelPlayable() {
    return next_level_playable;
  }
  
  public void togglePlayability() {
    next_level_playable = !next_level_playable;
  }
  
  public void drawMaze() {
    canvas.repaint();
  }
  
  public void paint(Graphics g) {
    if (constructing_maze) {
      paintConstruction(g);
    }
    else {
      paintTranversal(g);
    }
  }
  
  public void paintConstruction(Graphics g) {
    construction_displayer.paint(bufferg);
    g.drawImage(buffer, 0, 0, null);
  }
  
  public void paintTranversal(Graphics g) {
    if (center_ship == null) {
      return;
    }
    Point center_location = center_ship.getLocation();
    int start_row = Math.max(center_location.y - actual_sight_radius - 1, 0);
    int end_row = Math.min(center_location.y + actual_sight_radius + 1, rows - 1);
    int start_col = Math.max(center_location.x - actual_sight_radius - extra_cols, 0);
    int end_col = Math.min(center_location.x + actual_sight_radius + extra_cols, cols - 1);
    bufferg.setColor(Color.black);
    bufferg.fillRect(0, 0, dim.width, dim.height);
    bufferg.setColor(Color.gray);
    bufferg.setStroke(new BasicStroke(2));
    Point center_cell_corner =
            new Point((int) Math.round(center.x + cell_side_length
                    * (center_location.x - center_ship.getCol())), (int) Math.round(center.y
                    + cell_side_length * (center_location.y - center_ship.getRow())));
    for (int row = start_row, y = center_cell_corner.y - cell_side_length * (center_location.y - start_row); row <= end_row; ++row, y +=
            cell_side_length) {
      for (int col = start_col, x = center_cell_corner.x - cell_side_length * (center_location.x - start_col); col <= end_col; ++col, x +=
              cell_side_length) {
        if (revealed[col][row]) {
          if (maze.hasWall(col, row, CellSide.North)) {
            bufferg.drawLine(x, y, x + cell_side_length, y);
          }
          if (maze.hasWall(col, row, CellSide.West)) {
            bufferg.drawLine(x, y, x, y + cell_side_length);
          }
          if (maze.hasWall(col, row, CellSide.South)) {
            bufferg.drawLine(x, y + cell_side_length, x + cell_side_length, y + cell_side_length);
          }
          if (maze.hasWall(col, row, CellSide.East)) {
            bufferg.drawLine(x + cell_side_length, y, x + cell_side_length, y + cell_side_length);
          }
        }
      }
    }
    for (Scenery thing : scenery) {
      paintObject(bufferg, thing, unit_offset, center_cell_corner);
    }
    try {
      for (Powerup powerup : powerups) {
        paintObject(bufferg, powerup, powerup_offset, center_cell_corner);
      }
    }
    catch (ConcurrentModificationException cme) {
      System.out.print("powerups cme ");
    }
    try {
      for (Weapon shot : shots) {
        paintObject(bufferg, shot, unit_offset, center_cell_corner);
      }
    }
    catch (ConcurrentModificationException cme) {
      System.out.print("shots cme ");
    }
    try {
      for (Unit robot : robots) {
        paintObject(bufferg, robot, unit_offset, center_cell_corner);
      }
    }
    catch (ConcurrentModificationException cme) {
      System.out.print("robots cme ");
    }
    try {
      for (Pyro ship : ships) {
        Point location = ship.getLocation();
        if (!ship.isDestroyed() && revealed[location.x][location.y]) {
          ship.paint(images, bufferg, center_ship.getLocation(), center_cell_corner, cell_side_length,
                  unit_offset);
        }
      }
    }
    catch (ConcurrentModificationException cme) {
      System.out.print("ships cme ");
    }
    try {
      for (Transient tran : transients) {
        paintObject(bufferg, tran, unit_offset, center_cell_corner);
      }
    }
    catch (ConcurrentModificationException cme) {
      System.out.print("transients cme ");
    }
    if (!center_ship.isDestroyed()) {
      center_ship.paintInfo(bufferg, dim.width, descent_font);
    }
    g.drawImage(buffer, 0, 0, null);
  }
  
  public void paintObject(Graphics2D g, MazeObject object, int offset, Point center_cell_corner) {
    Point location = object.getLocation();
    if (revealed[location.x][location.y]) {
      object.paint(images, g, center_ship.getLocation(), center_cell_corner, cell_side_length, offset);
    }
  }
  
  public void revealMaze() {
    CellSide direction = center_ship.getDirection();
    CellSide[] sides = CellSide.adjacents(direction);
    Point current = center_ship.getLocation();
    while (maze.isValidCell(current.x, current.y)) {
      revealed[current.x][current.y] = true;
      for (CellSide dir : sides) {
        Point side = MazeWalker.getLocationInDirection(current.x, current.y, dir);
        if (maze.isValidCell(side.x, side.y)) {
          if (maze.hasWall(current.x, current.y, dir)) {
            if (!(maze.hasBarrier(current.x, current.y, dir))) {
              continue;
            }
          }
          revealed[side.x][side.y] = true;
        }
      }
      Point next = MazeWalker.getLocationInDirection(current.x, current.y, direction);
      if (maze.hasWall(current.x, current.y, direction)) {
        if (!(maze.hasBarrier(current.x, current.y, direction))) {
          break;
        }
      }
      current = next;
    }
  }
  
  public boolean isRevealed(Point location) {
    return revealed[location.x][location.y];
  }
  
  public void handleComponentResized(Dimension new_size) {
    if (!new_size.equals(dim)) {
      setSizes(new_size);
      readImages();
    }
  }
  
  public void handleKeyPressed(int key_code) {
    if (playable) {
      ((ClickToMovePyro) center_ship).handleKeyPressed(key_code);
    }
  }
  
  public void handleKeyReleased(int key_code) {
    if (playable && center_ship != null) {
      ((ClickToMovePyro) center_ship).handleKeyReleased(key_code);
    }
  }
  
  public void handleMousePressed(MouseEvent e) {
    if (playable && center_ship != null) {
      double col = center_ship.getCol() + ((double) (e.getX() - dim.width / 2) / cell_side_length);
      double row = center_ship.getRow() + ((double) (e.getY() - dim.height / 2) / cell_side_length);
      ((ClickToMovePyro) center_ship).handleMousePressed(e.getButton(), col, row, maze, revealed);
    }
  }
}
