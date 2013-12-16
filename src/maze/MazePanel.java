package maze;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import util.MusicPlayer;

public class MazePanel extends JPanel implements MazeCanvas, ComponentListener, KeyListener, MouseListener {
  private MazeDisplayer displayer;
  private MazeEngine engine;
  private final MusicPlayer music;
  private int music_level;
  private boolean music_playing;
  private boolean music_active;
  
  public MazePanel() {
    music = new MusicPlayer();
    addMouseListener(this);
  }
  
  @Override
  public void setDisplayer(MazeDisplayer displayer) {
    this.displayer = displayer;
  }
  
  @Override
  public void setEngine(MazeEngine engine) {
    this.engine = engine;
  }
  
  public int playMusic() {
    music_level = (int) (Math.random() * music.numLevels() + 1);
    if (music_active) {
      music.playMusic(music_level);
      music_playing = true;
    }
    return music_level;
  }
  
  public void stopMusic() {
    if (music_active) {
      music.stop();
      music_playing = false;
    }
  }
  
  public void closeMusic() {
    music.close();
  }
  
  @Override
  public void paint(Graphics g) {
    displayer.paint(g);
  }
  
  @Override
  public void componentHidden(ComponentEvent e) {
    
  }
  
  @Override
  public void componentMoved(ComponentEvent e) {
    
  }
  
  @Override
  public void componentResized(ComponentEvent e) {
    displayer.handleComponentResized(getSize());
  }
  
  @Override
  public void componentShown(ComponentEvent e) {
    
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
    int key_code = e.getKeyCode();
    switch (key_code) {
      case KeyEvent.VK_M:
        if (music_playing) {
          music.stop();
          music_playing = false;
          music_active = false;
        }
        else {
          music.playMusic(music_level);
          music_playing = true;
          music_active = true;
        }
        break;
      case KeyEvent.VK_S:
        engine.toggleSounds();
        break;
      case KeyEvent.VK_ENTER:
        displayer.togglePlayability();
        break;
      default:
        displayer.handleKeyPressed(key_code);
        break;
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e) {
    displayer.handleKeyReleased(e.getKeyCode());
  }
  
  @Override
  public void keyTyped(KeyEvent e) {
    
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    
  }
  
  @Override
  public void mouseEntered(MouseEvent e) {
    
  }
  
  @Override
  public void mouseExited(MouseEvent e) {
    
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    displayer.handleMousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    
  }
}
