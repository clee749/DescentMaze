package maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

public class MazeConstructionDisplayer {
  private MazeUtility maze;
  private int cols;
  private int rows;
  private int width;
  private int height;
  private int cell_side_length;
  private Point center;
  private Point center_location;
  
  public void setMaze(MazeUtility maze) {
    this.maze = maze;
    this.cols = maze.getCols();
    this.rows = maze.getRows();
    center_location = new Point(cols / 2, rows / 2);
  }
  
  public void setSizes(Dimension dim) {
    width = dim.width;
    height = dim.height;
    cell_side_length = Math.min(width / cols, height / rows) - 1;
    center = new Point(width / 2, height / 2);
  }
  
  public void paint(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(Color.black);
    g2d.fillRect(0, 0, width, height);
    g2d.setColor(Color.gray);
    g2d.setStroke(new BasicStroke(2));
    for (int row = 0, y = center.y - cell_side_length * center_location.y; row <= rows; ++row, y +=
            cell_side_length) {
      for (int col = 0, x = center.x - cell_side_length * center_location.x; col < cols; ++col, x +=
              cell_side_length) {
        if (maze.hasWall(col, row, CellSide.North)) {
          g2d.drawLine(x, y, x + cell_side_length, y);
        }
      }
    }
    for (int col = 0, x = center.x - cell_side_length * center_location.x; col <= cols; ++col, x +=
            cell_side_length) {
      for (int row = 0, y = center.y - cell_side_length * center_location.y; row < rows; ++row, y +=
              cell_side_length) {
        if (maze.hasWall(col, row, CellSide.West)) {
          g2d.drawLine(x, y, x, y + cell_side_length);
        }
      }
    }
  }
}
