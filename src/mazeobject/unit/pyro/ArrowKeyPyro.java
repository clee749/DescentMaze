package mazeobject.unit.pyro;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.ObjectType;

public class ArrowKeyPyro extends Pyro {
  private boolean is_moving;
  private CellSide next_direction;
  private boolean fire_primary;
  private boolean fire_secondary;
  private ObjectType secondary_weapon;
  private boolean drop_bomb;
  
  public ArrowKeyPyro(MazeEngine engine, Point location, CellSide direction) {
    super(engine, location, direction);
    next_direction = CellSide.South;
    secondary_weapon = ObjectType.Concussion;
  }
  
  @Override
  public boolean atExit(MazeUtility maze) {
    return (!exploded && location.y == maze.getRows() - 1 && location.x == maze.getExit());
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
    if (is_moving) {
      setDirection(next_direction);
      setVelocity(next_direction);
      if (dxdy.x != 0 || dxdy.y != 0) {
        location.x = (int) Math.round(col);
        location.y = (int) Math.round(row);
        if (dxdy.x != 0) {
          if ((dxdy.x > 0 && col < location.x) || (dxdy.x < 0 && col > location.x)) {
            col += (double) dxdy.x / num_transitions;
          }
          else {
            int testx = (int) (Math.round(col - 0.49 * dxdy.x));
            int prev_testy = (int) (Math.round(row - 0.3));
            int next_testy = (int) (Math.round(row + 0.3));
            if ((dxdy.x > 0 && col - 0.1 < location.x)
                    || (dxdy.x < 0 && col + 0.1 > location.x)
                    || (!maze.hasWall(testx, prev_testy, direction)
                            && !maze.hasWall(testx, next_testy, direction) && Math.abs(row - location.y) < 0.2)) {
              col += (double) dxdy.x / num_transitions;
            }
          }
        }
        else {
          if ((dxdy.y > 0 && row < location.y) || (dxdy.y < 0 && row > location.y)) {
            row += (double) dxdy.y / num_transitions;
          }
          int testy = (int) (Math.round(row - 0.49 * dxdy.y));
          int prev_testx = (int) (Math.round(col - 0.3));
          int next_testx = (int) (Math.round(col + 0.3));
          if ((dxdy.y > 0 && row - 0.1 < location.y)
                  || (dxdy.y < 0 && row + 0.1 > location.y)
                  || (!maze.hasWall(prev_testx, testy, direction)
                          && !maze.hasWall(next_testx, testy, direction) && Math.abs(col - location.x) < 0.2)) {
            row += (double) dxdy.y / num_transitions;
          }
        }
      }
    }
  }
  
  public MazeObject handleCannonFiring(int num_transitions) {
    MazeObject created = null;
    if (fire_primary) {
      created = fireCannon(engine);
      if (created != null) {
        reload_left *= num_transitions / cannon.shotsPerMove();
        if (reload_left == 0) {
          reload_left = num_transitions;
        }
      }
    }
    return created;
  }
  
  public MazeObject handleSecondaryFiring(int num_transitions) {
    MazeObject created = null;
    if (drop_bomb) {
      created = dropBomb();
      if (created != null) {
        bomb_reload *= num_transitions;
      }
    }
    else if (fire_secondary) {
      if (secondary_weapon.equals(ObjectType.Proximity)) {
        created = dropBomb();
        if (created != null) {
          bomb_reload *= num_transitions;
        }
      }
      else {
        created = fireMissile(secondary_weapon);
        if (created != null) {
          missile_reload *= num_transitions;
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
          missile_reload = 1;
          return true;
        }
        break;
      case Homing:
        if (num_homings > 0) {
          secondary_weapon = ObjectType.Homing;
          missile_reload = 1;
          return true;
        }
        break;
      case Proximity:
        if (num_bombs > 0) {
          secondary_weapon = ObjectType.Proximity;
          bomb_reload = 1;
          return true;
        }
        break;
      case Smart:
        if (num_smarts > 0) {
          secondary_weapon = ObjectType.Smart;
          missile_reload = 1;
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
  }
  
  @Override
  public void newLevel(Point location, CellSide direction) {
    super.newLevel(location, direction);
    dxdy.y = 1;
  }
  
  public boolean handleKeyPressed(int key_code) {
    switch (key_code) {
      case KeyEvent.VK_RIGHT:
        next_direction = CellSide.East;
        is_moving = true;
        return true;
      case KeyEvent.VK_UP:
        next_direction = CellSide.North;
        is_moving = true;
        return true;
      case KeyEvent.VK_LEFT:
        next_direction = CellSide.West;
        is_moving = true;
        return true;
      case KeyEvent.VK_DOWN:
        next_direction = CellSide.South;
        is_moving = true;
        return true;
        
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
    return false;
  }
  
  public void handleKeyReleased(int key_code) {
    switch (key_code) {
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_UP:
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_DOWN:
        is_moving = false;
        break;
      
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
}
