package util;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

public class MusicPlayer {
  private final String path;
  private Sequencer sequencer;
  
  public MusicPlayer(String path) {
    this.path = path;
    try {
      sequencer = MidiSystem.getSequencer();
      sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
    }
    catch (MidiUnavailableException e) {
      System.out.println("Cannot instantiate Midi device!");
    }
  }
  
  public MusicPlayer() {
    this("music");
  }
  
  public int numLevels() {
    return 22;
  }
  
  public void playMusic(int level) {
    if (sequencer == null) {
      return;
    }
    String filename = String.format("%s/GAME%02d.MID", path, level);
    URL midiFile = getClass().getClassLoader().getResource(filename);
    try {
      sequencer.setSequence(MidiSystem.getSequence(midiFile));
      if (!sequencer.isOpen()) {
        sequencer.open();
      }
      sequencer.start();
    }
    catch (MidiUnavailableException mue) {
      System.out.println("Midi device unavailable!");
    }
    catch (InvalidMidiDataException imde) {
      System.out.println("Invalid Midi data!");
    }
    catch (IOException ioe) {
      System.out.println("I/O Error!");
    }
  }
  
  public int playMusic() {
    int level = (int) (Math.random() * numLevels() + 1);
    playMusic(level);
    return level;
  }
  
  public void stop() {
    if (sequencer != null && sequencer.isOpen()) {
      sequencer.stop();
    }
  }
  
  public void close() {
    if (sequencer != null) {
      sequencer.close();
    }
  }
  
  public static void main(String[] args) throws Exception {
    MusicPlayer mp = new MusicPlayer();
    for (int i = 0; i < 2; ++i) {
      mp.playMusic();
      Thread.sleep(300000);
      mp.stop();
    }
    mp.close();
  }
}
