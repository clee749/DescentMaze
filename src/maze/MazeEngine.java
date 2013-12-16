package maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import mazeobject.MazeObject;
import mazeobject.MultipleObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Powerup;
import mazeobject.scenery.Entrance;
import mazeobject.scenery.Scenery;
import mazeobject.transients.Transient;
import mazeobject.unit.Unit;
import mazeobject.unit.pyro.ClickToMovePyro;
import mazeobject.unit.pyro.ComputerControlledPyro;
import mazeobject.unit.pyro.Pyro;
import mazeobject.unit.robot.Robot;
import mazeobject.unit.robot.ThreatLevel;
import mazeobject.weapon.Weapon;
import pilot.MazeWalker;
import pilot.RandomSolver;
import util.SoundPlayer;

public class MazeEngine {
  public static final int NUM_SHIFTS = 10;
  
  private MazeUtility maze;
  private int cols;
  private int rows;
  private ArrayList<Scenery> scenery;
  private ArrayList<Unit> robots;
  private ArrayList<Powerup> powerups;
  private ArrayList<Weapon> shots;
  private ArrayList<Transient> transients;
  private final ArrayList<Pyro> ships;
  private ArrayList<Unit>[][] robot_map;
  private final MazeDisplayer displayer;
  private Pyro center_ship;
  private int transitions_left;
  private LinkedList<Pyro> spawn_list;
  private boolean reset_score_on_respawn;
  private SoundPlayer sounds;
  private boolean sounds_active;
  private Robot previous_growler;
  
  public MazeEngine(MazeDisplayer displayer) {
    this.displayer = displayer;
    cols = -1;
    rows = -1;
    ships = new ArrayList<Pyro>();
  }
  
  public boolean soundsActive() {
    return sounds_active;
  }
  
  public void setInitials(MazeUtility maze, ArrayList<Scenery> scenery, ArrayList<Unit> robots,
          ArrayList<Powerup> powerups, int num_ships) {
    this.maze = maze;
    this.scenery = scenery;
    this.robots = robots;
    this.powerups = powerups;
    shots = new ArrayList<Weapon>();
    transients = new ArrayList<Transient>();
    for (Scenery thing : scenery) {
      if (thing.getType().equals(ObjectType.Entrance)) {
        spawn_list = ((Entrance) (thing)).getSpawnList();
        break;
      }
    }
    prepareRobotMap(maze.getCols(), maze.getRows());
    prepareShips(num_ships);
    preparePanel();
  }
  
  public boolean displayNextFrame() {
    if (transitions_left % NUM_SHIFTS == 0) {
      objectActions();
      if (center_ship.atExit(maze)) {
        center_ship.setDirection(CellSide.South);
        updateDisplay();
        return false;
      }
      transitions_left = NUM_SHIFTS;
    }
    transitionActions(NUM_SHIFTS);
    --transitions_left;
    updateDisplay();
    return true;
  }
  
  @SuppressWarnings("unchecked")
  public void prepareRobotMap(int new_cols, int new_rows) {
    if (rows == new_rows && cols == new_cols) {
      for (int row = 0; row < new_rows; ++row) {
        for (int col = 0; col < new_cols; ++col) {
          robot_map[col][row].clear();
        }
      }
    }
    else {
      robot_map = new ArrayList[new_cols][new_rows];
      for (int row = 0; row < new_rows; ++row) {
        for (int col = 0; col < new_cols; ++col) {
          robot_map[col][row] = new ArrayList<Unit>();
        }
      }
    }
    for (int i = 0; i < robots.size(); ++i) {
      Unit unit = robots.get(i);
      Point location = unit.getLocation();
      robot_map[location.x][location.y].add(unit);
    }
    cols = new_cols;
    rows = new_rows;
  }
  
  public void prepareShips(int num_ships) {
    if (displayer.nextLevelPlayable()) {
      if (!(center_ship instanceof ClickToMovePyro)) {
        ships.clear();
        center_ship = new ClickToMovePyro(this, new Point(maze.getEntrance(), 0), CellSide.South);
        center_ship.displayMessages(true);
        ships.add(center_ship);
      }
      else {
        center_ship.newLevel(new Point(maze.getEntrance(), 0), CellSide.South);
        center_ship.destroy();
      }
      spawn_list.add(center_ship);
    }
    else {
      if (center_ship instanceof ClickToMovePyro) {
        ships.clear();
      }
      while (ships.size() > num_ships) {
        ships.remove(ships.size() - 1);
      }
      while (ships.size() < num_ships) {
        ships.add(new ComputerControlledPyro(this, new Point(maze.getEntrance(), 0), CellSide.South));
      }
      center_ship = ships.get(0);
      center_ship.displayMessages(true);
      for (Pyro ship : ships) {
        if (ship instanceof ComputerControlledPyro) {
          ((ComputerControlledPyro) ship).setSolverAndPilot(new RandomSolver(maze));
        }
        ship.newLevel(new Point(maze.getEntrance(), 0), CellSide.South);
        ship.destroy();
        spawn_list.add(ship);
      }
    }
    for (Pyro ship : ships) {
      ship.resetScoreOnRespawn(reset_score_on_respawn);
    }
    center_ship.playPersonalSounds(sounds_active);
  }
  
  public void preparePanel() {
    displayer.setInitials(maze, cols, rows, scenery, robots, powerups, shots, transients, ships, center_ship);
    displayer.revealMaze();
  }
  
  public void updateDisplay() {
    displayer.drawMaze();
  }
  
  public void objectActions() {
    for (Pyro ship : ships) {
      if (ship.inMaze()) {
        ship.makeNextMove();
      }
    }
    displayer.revealMaze();
    for (int i = 0; i < robots.size(); ++i) {
      Unit unit = robots.get(i);
      addObject(unit.act(maze, this, ships));
    }
    for (Pyro ship : ships) {
      if (ship.inMaze()) {
        addObject(ship.act(maze, this, ships));
      }
      else if (!spawn_list.contains(ship) && !ship.atExit(maze)) {
        spawn_list.add(ship);
        if (ship instanceof ComputerControlledPyro) {
          ((ComputerControlledPyro) ship).setSolverAndPilot(((ComputerControlledPyro) ship).getSolver());
        }
      }
    }
    for (int i = 0; i < powerups.size(); ++i) {
      Powerup powerup = powerups.get(i);
      powerup.act(maze, this, ships);
      if (powerup.isDestroyed()) {
        powerups.remove(i);
        --i;
      }
    }
    for (Scenery thing : scenery) {
      addObject(thing.act(maze, this, ships));
    }
    if (sounds_active && Math.random() < 0.5) {
      findGrowlingRobot();
    }
    for (Pyro ship : ships) {
      if (!ship.equals(center_ship) && !ship.isDestroyed() && ship.atExit(maze)) {
        ship.destroy();
      }
    }
  }
  
  public void transitionActions(int num_shifts) {
    for (int i = 0; i < shots.size(); ++i) {
      Weapon shot = shots.get(i);
      addObject(shot.transitionAct(maze, this, ships, num_shifts));
      if (shot.isDestroyed()) {
        shots.remove(i);
        --i;
      }
    }
    for (Pyro ship : ships) {
      if (!ship.isDestroyed()) {
        addObject(ship.transitionAct(maze, this, ships, num_shifts));
      }
      if (ship.isDestroyed() && ship.getShields() < 0 && !ship.powerupReleased()) {
        addObject(ship.finalAction(maze, this, ships));
      }
    }
    for (int i = 0; i < robots.size(); ++i) {
      Unit unit = robots.get(i);
      addObject(unit.transitionAct(maze, this, ships, num_shifts));
      if (unit.isDestroyed()) {
        addObject(unit.finalAction(maze, this, ships));
        robots.remove(i);
        Point location = unit.getLocation();
        robot_map[location.x][location.y].remove(unit);
        location = unit.getNextLocation();
        if (location != null) {
          robot_map[location.x][location.y].remove(unit);
        }
        --i;
      }
    }
    for (Powerup powerup : powerups) {
      powerup.transitionAct(maze, this, ships, num_shifts);
    }
    for (int i = 0; i < transients.size(); ++i) {
      Transient tran = transients.get(i);
      addObject(tran.transitionAct(maze, this, ships, num_shifts));
      if (tran.isDestroyed()) {
        transients.remove(i);
        --i;
      }
    }
  }
  
  public void addObject(MazeObject object) {
    if (object == null) {
      return;
    }
    if (object.getLocation() != null && !maze.isValidCell(object.getLocation().x, object.getLocation().y)) {
      System.out.println("array index out of bounds!");
      System.out.println(object.getClass());
      System.out.println(object.getDirection());
      System.out.println(object.getLocation());
      System.out.println(object.getType());
    }
    if (object instanceof Weapon) {
      shots.add((Weapon) object);
    }
    else if (object instanceof Transient) {
      transients.add((Transient) object);
    }
    else if (object instanceof Powerup) {
      powerups.add((Powerup) object);
    }
    else if (object instanceof Unit && !object.getType().equals(ObjectType.Pyro)) {
      robots.add((Unit) object);
      Point location = object.getLocation();
      robot_map[location.x][location.y].add((Unit) object);
    }
    else if (object instanceof Scenery) {
      scenery.add((Scenery) object);
    }
    else if (object.getType().equals(ObjectType.MultipleObject)) {
      ArrayList<MazeObject> objects = ((MultipleObject) object).getObjects();
      for (MazeObject obj : objects) {
        addObject(obj);
      }
    }
  }
  
  public void doSplashDamage(double col, double row, int damage, MazeObject source, boolean source_immune,
          MazeObject already_hit) {
    int rounded_col = (int) Math.round(col);
    int rounded_row = (int) Math.round(row);
    Point location = new Point(rounded_col, rounded_row);
    HashSet<Unit> damaged = new HashSet<Unit>();
    for (Pyro ship : ships) {
      if (ship.equals(already_hit)) {
        continue;
      }
      if (!ship.isDestroyed()) {
        double distance = Math.hypot(ship.getRow() - row, ship.getCol() - col);
        if (distance < 0.5 && damaged.add(ship)) {
          ship.beDamaged(this, damage, source, true);
        }
        else if (distance < 1.0 && damaged.add(ship)) {
          ship.beDamaged(this, (int) ((1.0 - distance) * 2.0 * damage), source, true);
        }
      }
    }
    ArrayList<Point> connected = maze.findConnectedCells(location, 1);
    connected.add(location);
    for (Point current : connected) {
      Iterator<Unit> it = getRobots(current);
      if (it != null) {
        while (it.hasNext()) {
          Unit unit = it.next();
          if (unit.equals(already_hit) || (source_immune && unit.equals(source))) {
            continue;
          }
          double distance = Math.hypot(unit.getRow() - row, unit.getCol() - col);
          if (distance < 0.5 && damaged.add(unit)) {
            unit.beDamaged(this, damage, source, true);
          }
          else if (distance < 1.0 && damaged.add(unit)) {
            unit.beDamaged(this, (int) ((1.0 - distance) * 2.0 * damage), source, true);
          }
        }
      }
    }
  }
  
  public Unit getOccupant(Point location) {
    if (location.equals(center_ship.getLocation())) {
      return center_ship;
    }
    ArrayList<Unit> occupants = robot_map[location.x][location.y];
    if (occupants.size() > 0) {
      return occupants.get((int) (Math.random() * occupants.size()));
    }
    return null;
  }
  
  public Iterator<Unit> getRobots(Point location) {
    ArrayList<Unit> occupants = robot_map[location.x][location.y];
    if (occupants.size() > 0) {
      return occupants.iterator();
    }
    return null;
  }
  
  public Unit getVisibleRobot(Point location) {
    for (Unit unit : robot_map[location.x][location.y]) {
      if (unit.isVisible()) {
        return unit;
      }
    }
    return null;
  }
  
  public boolean isOccupied(Point location) {
    if (location.equals(center_ship.getLocation()) || containsRobot(location)) {
      return true;
    }
    return false;
  }
  
  public boolean containsRobot(Point location) {
    if (robot_map[location.x][location.y].size() > 0) {
      return true;
    }
    return false;
  }
  
  public boolean containsVisibleRobot(Point location) {
    Iterator<Unit> it = getRobots(location);
    if (it != null) {
      while (it.hasNext()) {
        if (it.next().isVisible()) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean containsUncloakedRobot(Point location) {
    Iterator<Unit> it = getRobots(location);
    if (it != null) {
      while (it.hasNext()) {
        if (!it.next().isCloaked()) {
          return true;
        }
      }
    }
    return false;
  }
  
  public int numRobots(Point location) {
    return robot_map[location.x][location.y].size();
  }
  
  public int numVisibleRobots(Point location) {
    Iterator<Unit> it = getRobots(location);
    int count = 0;
    if (it != null) {
      while (it.hasNext()) {
        if (it.next().isVisible()) {
          ++count;
        }
      }
    }
    return count;
  }
  
  public boolean containsUncloakedStrongRobot(Point location) {
    Iterator<Unit> it = getRobots(location);
    if (it != null) {
      while (it.hasNext()) {
        Unit unit = it.next();
        if (!unit.isCloaked() && unit instanceof Robot && !((Robot) unit).getThreat().equals(ThreatLevel.Low)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean containsVisibleHighThreat(Point location) {
    Iterator<Unit> it = getRobots(location);
    if (it != null) {
      while (it.hasNext()) {
        Unit unit = it.next();
        if (unit.isVisible() && unit instanceof Robot && ((Robot) unit).getThreat().equals(ThreatLevel.High)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void robotMoved(Robot robot, Point location, boolean into) {
    if (into) {
      robot_map[location.x][location.y].add(robot);
    }
    else {
      robot_map[location.x][location.y].remove(robot);
    }
  }
  
  public boolean isEnemy(Unit current, Unit target) {
    if (current.getType().equals(ObjectType.Pyro) && !target.getType().equals(ObjectType.Pyro)) {
      return true;
    }
    if (current instanceof Robot && target.getType().equals(ObjectType.Pyro)) {
      return true;
    }
    return false;
  }
  
  public boolean readyForReset() {
    return center_ship.maxedOut();
  }
  
  public void resetScoreOnRespawn(boolean reset_score_on_respawn) {
    this.reset_score_on_respawn = reset_score_on_respawn;
    for (Pyro ship : ships) {
      ship.resetScoreOnRespawn(reset_score_on_respawn);
    }
  }
  
  public void toggleSounds() {
    sounds_active = !sounds_active;
    if (sounds_active) {
      if (sounds == null) {
        sounds = new SoundPlayer("sounds/");
      }
    }
    if (center_ship != null) {
      center_ship.playPersonalSounds(sounds_active);
    }
  }
  
  public void playSound(String name) {
    playSound(name, null);
  }
  
  public void playSound(String name, Point from) {
    if (sounds_active) {
      float gain_reduction;
      if (from == null) {
        gain_reduction = 0.0f;
      }
      else {
        if (!displayer.isRevealed(from)) {
          return;
        }
        Point to = center_ship.getLocation();
        double distance = Math.hypot(from.x - to.x, from.y - to.y);
        if (distance > displayer.getSightRadius()) {
          return;
        }
        gain_reduction = Math.min(5.0f * (float) distance, 40.0f);
      }
      sounds.playSound(name, -gain_reduction);
    }
  }
  
  public void findGrowlingRobot() {
    if (!center_ship.isCloaked()) {
      HashSet<Unit> growlers = new HashSet<Unit>();
      Point center = center_ship.getLocation();
      addLiveRobots(growlers, center);
      for (CellSide direction : CellSide.values()) {
        Point current = center;
        int distance = 1;
        while (distance <= displayer.getSightRadius()
                && (!maze.hasWall(current, direction) || maze.hasBarrier(current.x, current.y, direction))) {
          current = MazeWalker.getLocationInDirection(current, direction);
          if (!maze.isValidCell(current.x, current.y)) {
            break;
          }
          addLiveRobots(growlers, current);
          ++distance;
        }
      }
      int size = growlers.size();
      if (size > 0) {
        Unit[] growlers_array = growlers.toArray(new Unit[size]);
        Robot growler = (Robot) growlers_array[(int) (Math.random() * size)];
        if (!growler.equals(previous_growler)) {
          playSound(growler.getGrowlSound(), growler.getLocation());
          previous_growler = growler;
          return;
        }
      }
    }
    previous_growler = null;
  }
  
  public void addLiveRobots(Collection<Unit> collection, Point location) {
    for (Unit unit : robot_map[location.x][location.y]) {
      if (!unit.isExploded() && unit instanceof Robot) {
        collection.add(unit);
      }
    }
  }
}
