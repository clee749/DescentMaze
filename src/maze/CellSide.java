package maze;

import java.awt.Point;

public enum CellSide {
  East, North, West, South;
  
  public static CellSide opposite(CellSide direction) {
    if (direction == null) {
      return null;
    }
    if (direction.equals(North)) {
      return South;
    }
    if (direction.equals(South)) {
      return North;
    }
    if (direction.equals(West)) {
      return East;
    }
    return West;
  }
  
  public static Point dxdy(CellSide direction) {
    if (direction != null) {
      if (direction.equals(North)) {
        return new Point(0, -1);
      }
      if (direction.equals(South)) {
        return new Point(0, 1);
      }
      if (direction.equals(West)) {
        return new Point(-1, 0);
      }
      if (direction.equals(East)) {
        return new Point(1, 0);
      }
    }
    return new Point(0, 0);
  }
  
  public static CellSide next(CellSide direction) {
    return CellSide.values()[(direction.ordinal() + 1) % 4];
  }
  
  public static CellSide[] adjacents(CellSide direction) {
    CellSide[] dirs;
    if (direction.equals(North) || direction.equals(South)) {
      dirs = new CellSide[] {West, East};
    }
    else {
      dirs = new CellSide[] {North, South};
    }
    return dirs;
  }
  
  public static CellSide direction(Point dxdy) {
    if (dxdy.x == 0 && dxdy.y == 0) {
      return null;
    }
    if (Math.abs(dxdy.x) > Math.abs(dxdy.y)) {
      if (dxdy.x > 0) {
        return East;
      }
      return West;
    }
    if (dxdy.y > 0) {
      return South;
    }
    return North;
  }
  
  public static CellSide bestDirection(double curx, double cury, double tarx, double tary,
          CellSide default_dir) {
    if ((Math.abs(curx - tarx) < 0.1) && (Math.abs(cury - tary) < 0.1)) {
      return default_dir;
    }
    if (Math.abs(tarx - curx) > Math.abs(tary - cury)) {
      if (tarx - curx > 0) {
        return East;
      }
      return West;
    }
    if (tary - cury > 0) {
      return South;
    }
    return North;
  }
}
