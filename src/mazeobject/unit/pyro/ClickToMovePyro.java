package mazeobject.unit.pyro;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;
import mazeobject.transients.Explosion;
import pilot.MazeWalker;

public class ClickToMovePyro extends Pyro {
  private int shifts_left;
  private boolean fire_primary;
  private boolean fire_secondary;
  private ObjectType secondary_weapon;
  private boolean drop_bomb;
  private LinkedList<CellSide> current_path;
  
  public ClickToMovePyro(MazeEngine engine, Point location, CellSide direction) {
    super(engine, location, direction);
    secondary_weapon = ObjectType.Concussion;
  }
  
  @Override
  public boolean atExit(MazeUtility maze) {
    return (!exploded && location.equals(next_location) && location.y == maze.getRows() - 1 && location.x == maze
            .getExit());
  }
  
  @Override
  public void makeNextMove() {
    
  }
  
  @Override
  public MazeObject act(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships) {
    handleSpecialStateExpiration();
    handleMessageExpiration();
    return null;
  }
  
  @Override
  public MazeObject transitionAct(MazeUtility maze, MazeEngine engine, ArrayList<Pyro> ships,
          int num_transitions) {
    if (shields < 0) {
      if (!exploded) {
        exploded = true;
        return new Explosion(col, row, 2 * num_transitions, 0.95);
      }
      --survival_left;
      if (survival_left <= 0) {
        is_destroyed = true;
      }
      return null;
    }
    --reload_left;
    --missile_reload;
    --bomb_reload;
    rechargeEnergy();
    handleMovement(maze, num_transitions);
    handleScoreIncrementExpiration();
    handleMissileLock();
    MazeObject created = handleCannonFiring(num_transitions);
    if (created != null) {
      return created;
    }
    return handleSecondaryFiring(num_transitions);
  }
  
  public void handleMovement(MazeUtility maze, int num_transitions) {
    if (shifts_left > 0) {
      col += (double) dxdy.x / num_transitions;
      row += (double) dxdy.y / num_transitions;
    }
    else if (current_path != null && !current_path.isEmpty()) {
      CellSide next_direction = current_path.removeFirst();
      setDirection(next_direction);
      setVelocity(next_direction);
      next_location = MazeWalker.getLocationInDirection(location, direction);
      shifts_left = num_transitions;
      col += (double) dxdy.x / num_transitions;
      row += (double) dxdy.y / num_transitions;
    }
    --shifts_left;
    location.x = (int) (Math.round(col));
    location.y = (int) (Math.round(row));
  }
  
  public MazeObject handleCannonFiring(int num_transitions) {
    MazeObject created = null;
    if (fire_primary) {
      created = fireCannon(engine);
      if (created != null) {
        reload_left = (reload_left + 1) * num_transitions / cannon.shotsPerMove();
      }
    }
    return created;
  }
  
  public MazeObject handleSecondaryFiring(int num_transitions) {
    MazeObject created = null;
    if (drop_bomb) {
      created = dropBomb();
      if (created != null) {
        bomb_reload = (bomb_reload + 1) * num_transitions;
      }
    }
    else if (fire_secondary) {
      if (secondary_weapon.equals(ObjectType.Proximity)) {
        created = dropBomb();
        if (created != null) {
          bomb_reload = (bomb_reload + 1) * num_transitions;
        }
      }
      else {
        created = fireMissile(secondary_weapon);
        if (created != null) {
          missile_reload = (missile_reload + 1) * num_transitions;
        }
      }
    }
    return created;
  }
  
  public boolean switchSecondary(ObjectType next) {
    switch (next) {
      case Concussion:
        if (num_concussions > 0) {
          secondary_weapon = ObjectType.Concussion;
          missile_reload *= MazeEngine.NUM_SHIFTS;
          return true;
        }
        break;
      case Homing:
        if (num_homings > 0) {
          secondary_weapon = ObjectType.Homing;
          missile_reload *= MazeEngine.NUM_SHIFTS;
          return true;
        }
        break;
      case Proximity:
        if (num_bombs > 0) {
          secondary_weapon = ObjectType.Proximity;
          bomb_reload *= MazeEngine.NUM_SHIFTS;
          return true;
        }
        break;
      case Smart:
        if (num_smarts > 0) {
          secondary_weapon = ObjectType.Smart;
          missile_reload *= MazeEngine.NUM_SHIFTS;
          return true;
        }
        break;
    }
    return false;
  }
  
  @Override
  public void spawn(Point location, CellSide direction) {
    super.spawn(location, direction);
    dxdy.y = 1;
    shifts_left = 0;
    fire_primary = false;
    fire_secondary = false;
    secondary_weapon = ObjectType.Concussion;
    drop_bomb = false;
    current_path = null;
  }
  
  @Override
  public void newLevel(Point location, CellSide direction) {
    super.newLevel(location, direction);
    dxdy.y = 1;
    shifts_left = 0;
  }
  
  public void handleKeyPressed(int key_code) {
    switch (key_code) {
      case KeyEvent.VK_CONTROL:
        fire_primary = true;
        break;
      case KeyEvent.VK_SPACE:
        fire_secondary = true;
        break;
      case KeyEvent.VK_B:
        drop_bomb = true;
        break;
      
      case KeyEvent.VK_1:
        switchCannon(ObjectType.LaserCannon, true);
        break;
      case KeyEvent.VK_4:
        switchCannon(ObjectType.PlasmaCannon, true);
        break;
      case KeyEvent.VK_5:
        switchCannon(ObjectType.FusionCannon, true);
        break;
      
      case KeyEvent.VK_6:
        switchSecondary(ObjectType.Concussion);
        break;
      case KeyEvent.VK_7:
        switchSecondary(ObjectType.Homing);
        break;
      case KeyEvent.VK_8:
        switchSecondary(ObjectType.Proximity);
        break;
      case KeyEvent.VK_9:
        switchSecondary(ObjectType.Smart);
        break;
    }
  }
  
  public void handleKeyReleased(int key_code) {
    switch (key_code) {
      case KeyEvent.VK_CONTROL:
        fire_primary = false;
        break;
      case KeyEvent.VK_SPACE:
        fire_secondary = false;
        break;
      case KeyEvent.VK_B:
        drop_bomb = false;
        break;
    }
  }
  
  public void handleMousePressed(int button, double col, double row, MazeUtility maze, boolean[][] revealed) {
    switch (button) {
      case 1: // left mouse button: go there
        current_path =
                MazeWalker.findPath(maze, revealed, next_location, new Point((int) Math.round(col),
                        (int) Math.round(row)));
        break;
      case 3: // right mouse button: face there
        if (shifts_left < 1 && (current_path == null || current_path.isEmpty())) {
          setDirection(CellSide.bestDirection(this.col, this.row, col, row, CellSide.East));
          dxdy = CellSide.dxdy(direction);
        }
        break;
    }
  }
}
