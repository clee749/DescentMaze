package mazeobject.unit.pyro;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import maze.CellSide;
import maze.MazeEngine;
import maze.MazeUtility;
import mazeobject.MazeObject;
import mazeobject.MultipleObject;
import mazeobject.ObjectType;
import mazeobject.powerup.Cloak;
import mazeobject.powerup.ConcussionMissile;
import mazeobject.powerup.ConcussionPack;
import mazeobject.powerup.Energy;
import mazeobject.powerup.HomingMissile;
import mazeobject.powerup.HomingPack;
import mazeobject.powerup.Powerup;
import mazeobject.powerup.ProximityPack;
import mazeobject.powerup.QuadLasers;
import mazeobject.powerup.Shield;
import mazeobject.powerup.SmartMissile;
import mazeobject.powerup.cannon.Cannon;
import mazeobject.powerup.cannon.LaserCannon;
import mazeobject.unit.Proximity;
import mazeobject.unit.Unit;
import mazeobject.weapon.Concussion;
import mazeobject.weapon.Homing;
import mazeobject.weapon.Smart;
import mazeobject.weapon.Weapon;
import util.ImageHandler;

public abstract class Pyro extends Unit {
  public static final double DUAL = 0.16;
  public static final double QUAD = 0.24;
  public static final double QUAD2 = 0.05;
  public static final double MISSILE_OFFSET = 0.04;
  public static final Color INVULNERABILITY_COLOR = new Color(Color.yellow.getColorSpace(),
          Color.yellow.getColorComponents(null), 0.7f);
  public static final Color[] SHIELD_COLORS = new Color[10];
  public static final Color[] FADING_SCORE_COLORS = new Color[10];
  
  protected MazeEngine engine;
  protected int energy;
  protected int energy_recharged;
  protected boolean cloaked;
  protected int cloak_left;
  protected boolean visible;
  protected boolean invulnerable;
  protected int invulnerability_left;
  protected boolean quad_lasers;
  protected HashMap<ObjectType, Cannon> cannons;
  protected int missile_side;
  protected int missile_reload;
  protected int bomb_reload;
  protected int num_concussions;
  protected int num_homings;
  protected int num_bombs;
  protected int num_smarts;
  protected boolean maxed_out;
  protected boolean spawning;
  protected boolean powerup_released;
  protected boolean display_messages;
  protected final LinkedList<String> messages;
  protected int message_ttl;
  protected int total_score;
  protected int score_increment;
  protected int score_increment_ttl;
  protected boolean reset_score_on_respawn;
  protected boolean play_personal_sounds;
  protected boolean being_tracked;
  protected int missile_lock_timer;
  protected int missile_lock_interval;
  
  public Pyro(MazeEngine engine, Point location, CellSide direction) {
    super(location, direction, 100);
    if (SHIELD_COLORS[0] == null) {
      initShieldColors();
    }
    if (FADING_SCORE_COLORS[0] == null) {
      initFadingScoreColors();
    }
    this.engine = engine;
    next_location = new Point(location);
    energy = 100;
    visible = true;
    cannon = new LaserCannon(2.7, 5, 1);
    reload_left = 1;
    cannons = new HashMap<ObjectType, Cannon>();
    cannons.put(ObjectType.LaserCannon, cannon);
    missile_side = (int) (Math.random() * 2);
    missile_reload = 1;
    bomb_reload = 1;
    num_concussions = 3;
    spawning = true;
    messages = new LinkedList<String>();
    missile_lock_interval = -1;
  }
  
  public void initShieldColors() {
    for (int i = 0; i < SHIELD_COLORS.length; ++i) {
      SHIELD_COLORS[i] =
              new Color(Color.cyan.getColorSpace(), Color.cyan.getColorComponents(null), 0.05f * (i + 1));
    }
  }
  
  public void initFadingScoreColors() {
    for (int i = 0; i < SHIELD_COLORS.length; ++i) {
      FADING_SCORE_COLORS[i] =
              new Color(Color.green.getColorSpace(), Color.green.getColorComponents(null), 0.1f * i + 0.05f);
    }
  }
  
  public int getEnergy() {
    return energy;
  }
  
  @Override
  public ObjectType getType() {
    return ObjectType.Pyro;
  }
  
  @Override
  public boolean isVisible() {
    return visible;
  }
  
  @Override
  public boolean isCloaked() {
    return cloaked;
  }
  
  public void setVelocity(CellSide direction) {
    dxdy = CellSide.dxdy(direction);
  }
  
  public void setVelocity(int dx, int dy) {
    dxdy = new Point(Math.min(dx, 1), Math.min(dy, 1));
    next_location = new Point(location.x + dxdy.x, location.y + dxdy.y);
  }
  
  public int getLaserLevel() {
    return ((LaserCannon) cannons.get(ObjectType.LaserCannon)).getLevel();
  }
  
  public boolean maxedOut() {
    return maxed_out;
  }
  
  public boolean inMaze() {
    return !exploded && !is_destroyed;
  }
  
  public boolean powerupReleased() {
    return powerup_released;
  }
  
  public void displayMessages(boolean display_messages) {
    this.display_messages = display_messages;
  }
  
  public void resetScoreOnRespawn(boolean reset_score_on_respawn) {
    this.reset_score_on_respawn = reset_score_on_respawn;
  }
  
  public void playPersonalSounds(boolean play_personal_sounds) {
    this.play_personal_sounds = play_personal_sounds;
  }
  
  public boolean personalSoundsEnabled() {
    return play_personal_sounds;
  }
  
  @Override
  public void paint(ImageHandler images, Graphics2D g, Point ship_location, Point center_cell_corner,
          int side_length, int offset) {
    int x = (int) Math.round(center_cell_corner.x - side_length * (ship_location.x - col));
    int y = (int) Math.round(center_cell_corner.y - side_length * (ship_location.y - row));
    if (!cloaked) {
      paintShield(g, x, y, offset, side_length);
      g.drawImage(getImage(images), x + offset, y + offset, null);
    }
  }
  
  public void paintShield(Graphics2D g, int x, int y, int offset, int side_length) {
    if (invulnerable) {
      g.setColor(INVULNERABILITY_COLOR);
    }
    else if (shields > 9) {
      g.setColor(SHIELD_COLORS[Math.min(shields / 10, 10) - 1]);
    }
    else {
      return;
    }
    g.fillOval(x + offset / 2, y + offset / 2, side_length - offset, side_length - offset);
  }
  
  public void paintInfo(Graphics2D g, int canvas_width, Font font) {
    g.setFont(font);
    g.setColor(Color.yellow);
    g.drawString("Energy: " + energy, 10, 20);
    g.setColor(Color.cyan);
    g.drawString("Shield: " + shields, 10, 40);
    int text_offset =
            paintCannonInfo(g, ObjectType.LaserCannon, 70, "Laser Lvl: " + getLaserLevel()
                    + (quad_lasers ? " Quad" : ""));
    text_offset = paintCannonInfo(g, ObjectType.PlasmaCannon, text_offset, "Plasma");
    text_offset = paintCannonInfo(g, ObjectType.FusionCannon, text_offset, "Fusion");
    g.setColor(Color.green);
    text_offset += 10;
    if (num_concussions > 0) {
      paintSecondaryWeaponInfo(g, text_offset, "Concsn Missile: ", num_concussions);
      text_offset += 20;
    }
    if (num_homings > 0) {
      paintSecondaryWeaponInfo(g, text_offset, "Homing Missile: ", num_homings);
      text_offset += 20;
    }
    if (num_bombs > 0) {
      paintSecondaryWeaponInfo(g, text_offset, "Proxim. Bomb: ", num_bombs);
      text_offset += 20;
    }
    if (num_smarts > 0) {
      paintSecondaryWeaponInfo(g, text_offset, "Smart Missile: ", num_smarts);
      text_offset += 20;
    }
    paintScore(g, canvas_width);
    if (display_messages) {
      paintMessages(g, canvas_width);
    }
  }
  
  public int paintCannonInfo(Graphics2D g, ObjectType cannon_type, int text_offset, String cannon_text) {
    if (cannons.containsKey(cannon_type)) {
      if (this.cannon.getType().equals(cannon_type)) {
        g.setColor(Color.green);
      }
      else {
        g.setColor(Color.gray);
      }
      g.drawString(cannon_text, 10, text_offset);
      return text_offset + 20;
    }
    return text_offset;
  }
  
  public void paintSecondaryWeaponInfo(Graphics2D g, int text_offset, String weapon_text, int num) {
    FontMetrics metrics = g.getFontMetrics();
    g.setColor(Color.green);
    g.drawString(weapon_text, 10, text_offset);
    g.setColor(Color.red);
    g.drawString(String.format("%03d", num), 10 + metrics.stringWidth(weapon_text), text_offset);
  }
  
  public void paintScore(Graphics2D g, int canvas_width) {
    g.setColor(Color.green);
    g.drawString("Score: " + total_score, canvas_width - 100, 20);
    if (score_increment > 0) {
      if (score_increment_ttl < 3 * MazeEngine.NUM_SHIFTS) {
        g.setColor(FADING_SCORE_COLORS[score_increment_ttl / 3]);
      }
      g.drawString(String.valueOf(score_increment), canvas_width - 60, 40);
    }
  }
  
  public void paintMessages(Graphics2D g, int canvas_width) {
    int center_x = canvas_width / 2;
    int offset_y = 20;
    g.setColor(Color.green);
    FontMetrics metrics = g.getFontMetrics();
    for (String message : messages) {
      int offset_x = center_x - metrics.stringWidth(message) / 2;
      g.drawString(message, offset_x, offset_y);
      offset_y += 20;
    }
  }
  
  @Override
  public void beDamaged(MazeEngine engine, int amount, MazeObject source, boolean is_splash) {
    if (invulnerable) {
      if (!is_splash) {
        playPublicSound("effects/doorhit.wav");
      }
    }
    else {
      shields -= amount;
      if (!is_splash) {
        playPublicSound("effects/shit01.wav");
      }
      if (shields < 0 && !exploded) {
        action_reload_left = -1;
        survival_left = MazeEngine.NUM_SHIFTS / 3;
        playPublicSound("effects/explode2.wav");
      }
    }
    visible = true;
  }
  
  @Override
  public MazeObject fireCannon(MazeEngine engine) {
    int cost = cannon.getEnergyCost();
    boolean quad = quad_lasers && (cannon.getType().equals(ObjectType.LaserCannon));
    if (quad) {
      cost *= 2;
    }
    if (reload_left > 0 || cost > energy) {
      return null;
    }
    useEnergy(cost);
    reload_left = cannon.getReload();
    visible = true;
    MultipleObject shots = new MultipleObject();
    Point traverse = new Point(dxdy.y, dxdy.x);
    shots.add(cannon.shoot(this, col - DUAL * traverse.x, row - DUAL * traverse.y, direction));
    shots.add(cannon.shoot(this, col + DUAL * traverse.x, row + DUAL * traverse.y, direction));
    if (quad) {
      shots.add(cannon.shoot(this, col - QUAD * traverse.x + QUAD2 * traverse.y, row - QUAD * traverse.y
              + QUAD2 * traverse.x, direction));
      shots.add(cannon.shoot(this, col + QUAD * traverse.x + QUAD2 * traverse.y, row + QUAD * traverse.y
              + QUAD2 * traverse.x, direction));
    }
    playPublicSound(cannon.getShootingSound(true));
    return shots;
  }
  
  public Weapon fireMissile(ObjectType type) {
    if (missile_reload > 0) {
      return null;
    }
    double offset = ((missile_side % 2) * 2 - 1) * MISSILE_OFFSET;
    Point dxdy = CellSide.dxdy(CellSide.next(direction));
    Weapon missile = null;
    switch (type) {
      case Concussion:
        if (num_concussions > 0) {
          --num_concussions;
          ++missile_side;
          missile = new Concussion(this, col - offset * dxdy.x, row - offset * dxdy.y, direction, 2.5, 16);
        }
        break;
      case Homing:
        if (num_homings > 0) {
          --num_homings;
          ++missile_side;
          missile = new Homing(this, col - offset * dxdy.x, row - offset * dxdy.y, direction, 2.0, 16);
        }
        break;
      case Smart:
        if (num_smarts > 0) {
          --num_smarts;
          missile = new Smart(this, col, row, direction, 2.0, 10);
        }
        break;
    }
    if (missile != null) {
      missile_reload = 1;
      visible = true;
      if (num_concussions < 1 && num_homings < 1 && num_smarts < 1) {
        addMessage("No secondary weapons available!");
      }
      playPublicSound("weapons/missile1.wav");
    }
    return missile;
  }
  
  public Proximity dropBomb() {
    if (num_bombs > 0 && bomb_reload < 1) {
      --num_bombs;
      bomb_reload = 1;
      visible = true;
      playPublicSound("weapons/dropbomb.wav");
      return new Proximity(this, location, 27);
    }
    return null;
  }
  
  public MultipleObject addShot(MultipleObject shots, MazeObject shot) {
    if (shots == null) {
      shots = new MultipleObject();
    }
    shots.add(shot);
    return shots;
  }
  
  public void recharge() {
    energy_recharged = Math.min(MazeEngine.NUM_SHIFTS, 100 - energy);
    if (energy_recharged > 0) {
      playPersonalSound("effects/power04.wav");
    }
  }
  
  public void rechargeEnergy() {
    if (energy_recharged > 0) {
      ++energy;
      --energy_recharged;
    }
  }
  
  public boolean acquirePowerup(Powerup powerup) {
    if (powerup instanceof Cannon) {
      return acquireCannon((Cannon) powerup);
    }
    switch (powerup.getType()) {
      case Shield:
        return addShields(18);
      case Energy:
        return addEnergy(18);
      case QuadLasers:
        return acquireQuadLasers();
      case Cloak:
        return cloak(60);
      case Invulnerability:
        return becomeInvulnerable(60);
      case ConcussionMissile:
        return acquireConcussions(1);
      case ConcussionPack:
        return acquireConcussions(4);
      case HomingMissile:
        return acquireHomings(1);
      case HomingPack:
        return acquireHomings(4);
      case ProximityPack:
        return acquireBombs();
      case SmartMissile:
        return acquireSmart();
      default:
        return false;
    }
  }
  
  public boolean addShields(int amount) {
    if (shields < 200) {
      shields = Math.min(shields + amount, 200);
      addMessage("Shield boosted to " + shields);
      return true;
    }
    addMessage("Your Shield is maxed out!");
    return false;
  }
  
  public boolean addEnergy(int amount) {
    if (energy < 200) {
      energy = Math.min(energy + amount, 200);
      addMessage("Energy boosted to " + energy);
      return true;
    }
    addMessage("Your Energy is maxed out!");
    return false;
  }
  
  public void useEnergy(int amount) {
    energy -= amount;
  }
  
  public boolean acquireCannon(Cannon cannon) {
    ObjectType type = cannon.getType();
    if (type.equals(ObjectType.LaserCannon)) {
      return acquireLasers();
    }
    if (!cannons.containsKey(type)) {
      cannons.put(type, cannon);
      addMessage(cannon.getMessageSubstring() + "!");
      return true;
    }
    addMessage("You already have the " + cannon.getMessageSubstring() + "!");
    return addEnergy(18);
  }
  
  public void setActionReload(int shots_per_move) {
    if (shots_per_move == 1) {
      action_reload = 0;
    }
    else {
      action_reload = (int) Math.ceil(MazeEngine.NUM_SHIFTS / shots_per_move);
    }
  }
  
  public boolean acquireQuadLasers() {
    if (!quad_lasers) {
      quad_lasers = true;
      addMessage("Quad Lasers!");
      return true;
    }
    addMessage("You already have Quad Lasers!");
    return addEnergy(18);
  }
  
  public boolean acquireLasers() {
    LaserCannon lasers = (LaserCannon) cannons.get(ObjectType.LaserCannon);
    if (lasers.addLevel()) {
      addMessage("Laser boosted to " + lasers.getLevel());
      return true;
    }
    maxed_out = true;
    addMessage("Your Laser is maxed out!");
    return addEnergy(18);
  }
  
  public boolean cloak(int timesteps) {
    if (cloaked) {
      addMessage("You already are cloaked!");
      return false;
    }
    cloaked = true;
    cloak_left = timesteps;
    visible = false;
    addMessage("Cloaking Device!");
    return true;
  }
  
  public boolean becomeInvulnerable(int timesteps) {
    if (invulnerable) {
      addMessage("You already are invulnerable!");
      return false;
    }
    invulnerable = true;
    invulnerability_left = timesteps;
    addMessage("Invulnerability!");
    return true;
  }
  
  public boolean acquireConcussions(int num) {
    if (num_concussions < 20) {
      int num_acquired = Math.min(num_concussions + num, 20) - num_concussions;
      num_concussions += num_acquired;
      if (num > 1) {
        addMessage(num_acquired + " Concussion Missiles!");
      }
      else {
        addMessage("Concussion Missile!");
      }
      return true;
    }
    addMessage("You already have 20 Concussion Missiles!");
    return false;
  }
  
  public boolean acquireHomings(int num) {
    if (num_homings < 10) {
      int num_acquired = Math.min(num_homings + num, 10) - num_homings;
      num_homings += num_acquired;
      if (num > 1) {
        addMessage(num_acquired + " Homing Missiles!");
      }
      else {
        addMessage("Homing Missile!");
      }
      return true;
    }
    addMessage("You already have 10 Homing Missiles!");
    return false;
  }
  
  public boolean acquireBombs() {
    if (num_bombs < 10) {
      int num_acquired = Math.min(num_bombs + 4, 10) - num_bombs;
      num_bombs += num_acquired;
      addMessage(num_acquired + " Proximity Bombs!");
      return true;
    }
    addMessage("You already have 10 Proximity Bombs!");
    return false;
  }
  
  public boolean acquireSmart() {
    if (num_smarts < 5) {
      ++num_smarts;
      addMessage("Smart Missile!");
      return true;
    }
    addMessage("You already have 5 Smart Missiles!");
    return false;
  }
  
  public Powerup releaseRandomPowerup() {
    powerup_released = true;
    Point loc = new Point(location.x + dxdy.x, location.y + dxdy.y);
    if (cloaked) {
      return new Cloak(loc);
    }
    ArrayList<Powerup> list = new ArrayList<Powerup>();
    if (num_smarts > 0) {
      list.add(new SmartMissile(loc));
    }
    if (num_bombs > 3) {
      list.add(new ProximityPack(loc));
    }
    if (num_homings > 3) {
      list.add(new HomingPack(loc));
    }
    if (num_concussions > 3) {
      list.add(new ConcussionPack(loc));
    }
    if (num_homings > 0) {
      list.add(new HomingMissile(loc));
    }
    if (num_concussions > 0) {
      list.add(new ConcussionMissile(loc));
    }
    Cannon released = cannons.get(ObjectType.FusionCannon);
    if (released != null) {
      released.releaseAsPowerup(loc);
      list.add(released);
    }
    released = cannons.get(ObjectType.PlasmaCannon);
    if (released != null) {
      released.releaseAsPowerup(loc);
      list.add(released);
    }
    if (quad_lasers) {
      list.add(new QuadLasers(loc));
    }
    if (getLaserLevel() > 1) {
      released = cannons.get(ObjectType.LaserCannon);
      released.releaseAsPowerup(loc);
      list.add(released);
    }
    if (energy > 17) {
      list.add(new Energy(loc));
    }
    else {
      list.add(new Shield(loc));
    }
    return list.get((int) (Math.random() * list.size()));
  }
  
  @Override
  public Powerup releasePowerup() {
    powerup_released = true;
    Point loc = new Point(location.x + dxdy.x, location.y + dxdy.y);
    if (cloaked) {
      return new Cloak(loc);
    }
    if (num_smarts > 0) {
      return new SmartMissile(loc);
    }
    if (num_bombs > 3) {
      return new ProximityPack(loc);
    }
    if (num_homings > 3) {
      return new HomingPack(loc);
    }
    if (num_concussions > 3) {
      return new ConcussionPack(loc);
    }
    if (num_homings > 0) {
      return new HomingMissile(loc);
    }
    if (num_concussions > 0) {
      return new ConcussionMissile(loc);
    }
    Cannon released = cannons.get(ObjectType.FusionCannon);
    if (released != null) {
      released.releaseAsPowerup(loc);
      return released;
    }
    released = cannons.get(ObjectType.PlasmaCannon);
    if (released != null) {
      released.releaseAsPowerup(loc);
      return released;
    }
    if (quad_lasers) {
      return new QuadLasers(loc);
    }
    if (getLaserLevel() > 1) {
      released = cannons.get(ObjectType.LaserCannon);
      released.releaseAsPowerup(loc);
      return released;
    }
    if (energy > 17) {
      return new Energy(loc);
    }
    return new Shield(loc);
  }
  
  public boolean switchCannon(ObjectType next, boolean displayFailureMessage) {
    if (cannon.getType().equals(next)) {
      return true;
    }
    Cannon actual = cannons.get(next);
    if (actual != null) {
      cannon = actual;
      reload_left = 1;
      setActionReload(cannon.shotsPerMove());
      addMessage(cannon.getMessageSubstring() + " selected!");
      playPersonalSound("effects/change1.wav");
      return true;
    }
    if (displayFailureMessage) {
      addMessage("You don't have the " + Cannon.getMessageSubstring(next) + "!");
      playPersonalSound("effects/beep02.wav");
    }
    return false;
  }
  
  public void handleSpecialStateExpiration() {
    if (cloaked) {
      visible = false;
      if (--cloak_left == 0) {
        cloaked = false;
        visible = true;
        playPersonalSound("effects/cloakoff.wav");
      }
    }
    if (invulnerable) {
      if (--invulnerability_left == 0) {
        invulnerable = false;
        playPersonalSound("effects/invoff.wav");
      }
    }
  }
  
  public void handleMessageExpiration() {
    if (!messages.isEmpty()) {
      if (message_ttl < 1) {
        messages.removeFirst();
        message_ttl = 10;
      }
      else {
        --message_ttl;
      }
    }
  }
  
  public void addMessage(String message) {
    if (display_messages) {
      messages.add(message);
      if (messages.size() > 3) {
        messages.removeFirst();
      }
      message_ttl = 10;
    }
  }
  
  public void handleScoreIncrementExpiration() {
    if (score_increment_ttl < 1 && score_increment > 0) {
      score_increment = 0;
    }
    else {
      --score_increment_ttl;
    }
  }
  
  public void incrementScore(int amount) {
    total_score += amount;
    score_increment += amount;
    score_increment_ttl = 10 * MazeEngine.NUM_SHIFTS;
  }
  
  public void resetScore() {
    total_score = 0;
    score_increment = 0;
  }
  
  public boolean playPersonalSound(String name) {
    if (play_personal_sounds) {
      engine.playSound(name);
      return true;
    }
    return false;
  }
  
  public void playPublicSound(String name) {
    if (!playPersonalSound(name)) {
      engine.playSound(name, location);
    }
  }
  
  public void handleMissileLock() {
    if (being_tracked) {
      being_tracked = false;
      if (missile_lock_timer >= missile_lock_interval) {
        playPersonalSound("weapons/misslock.wav");
        missile_lock_timer = 0;
      }
      else {
        ++missile_lock_timer;
      }
    }
    else {
      missile_lock_interval = -1;
    }
  }
  
  public void missileLocked(double from_col, double from_row) {
    int distance = (int) Math.round(Math.abs(from_col - col) + Math.abs(from_row - row));
    if (missile_lock_interval < 0) {
      missile_lock_timer = distance;
    }
    if (!being_tracked || distance < missile_lock_interval) {
      missile_lock_interval = distance;
    }
    being_tracked = true;
  }
  
  public void spawn(Point location, CellSide direction) {
    messages.clear();
    if (exploded) {
      shields = 100;
      energy = 100;
      cannon = new LaserCannon(2.7, 5, 1);
      action_reload = 0;
      quad_lasers = false;
      cannons = new HashMap<ObjectType, Cannon>();
      cannons.put(ObjectType.LaserCannon, cannon);
      missile_side = (int) (Math.random() * 2);
      num_concussions = 3;
      num_homings = 0;
      num_bombs = 0;
      num_smarts = 0;
      maxed_out = false;
      addMessage("Ship destroyed!");
      if (reset_score_on_respawn) {
        resetScore();
      }
    }
    this.location = location;
    this.direction = direction;
    row = location.y;
    col = location.x;
    dxdy = new Point(0, 0);
    next_location = new Point(location);
    energy_recharged = 0;
    cloaked = false;
    visible = true;
    invulnerable = false;
    reload_left = 1;
    missile_reload = 1;
    bomb_reload = 1;
    spawning = true;
    exploded = false;
    powerup_released = false;
    is_destroyed = false;
    score_increment_ttl = 10 * MazeEngine.NUM_SHIFTS;
    being_tracked = false;
    missile_lock_interval = -1;
  }
  
  public void newLevel(Point location, CellSide direction) {
    this.location = location;
    this.direction = direction;
    row = location.y;
    col = location.x;
    next_location = new Point(location);
    dxdy = new Point(0, 0);
    energy = Math.max(energy, 100);
    shields = Math.max(shields, 100);
    num_concussions = Math.max(num_concussions, 3);
    energy_recharged = 0;
    cloaked = false;
    visible = true;
    invulnerable = false;
    reload_left = 1;
    action_reload_left = -1;
    missile_reload = 1;
    bomb_reload = 1;
    spawning = true;
    messages.clear();
    score_increment = 0;
    score_increment_ttl = 0;
    being_tracked = false;
    missile_lock_interval = -1;
  }
  
  public abstract boolean atExit(MazeUtility maze);
  
  public abstract void makeNextMove();
}
