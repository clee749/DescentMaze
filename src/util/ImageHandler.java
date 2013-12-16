package util;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import maze.CellSide;
import mazeobject.ObjectType;

// Powerups: http://www.r1ch.net/old/descent/weapons.htm
// Robots: http://www.descent2.com/goodies/3dmodels/thinman/descent1

public class ImageHandler {
  private HashMap<String, ArrayList<Image>> images;
  
  public void readImages(String path, int scenery_size, int unit_size, int weapon_size, int powerup_size) {
    images = new HashMap<String, ArrayList<Image>>();
    for (ObjectType scenery : ObjectType.getScenery()) {
      addImage(path, scenery.name(), scenery_size);
    }
    for (ObjectType robot : ObjectType.getRobots()) {
      addImage(path, robot.name(), unit_size);
    }
    for (ObjectType weapon : ObjectType.getWeapons()) {
      addImage(path, weapon.name(), weapon_size);
    }
    for (ObjectType powerup : ObjectType.getPowerups()) {
      addImage(path, powerup.name(), powerup_size);
    }
    addImage(path, "Pyro", unit_size);
    for (int level = 1; level <= 4; ++level) {
      for (CellSide direction : CellSide.values()) {
        addImage(path, "Laser" + String.valueOf(level) + direction.name(), weapon_size);
      }
    }
  }
  
  public boolean addImage(String path, String name, int size) {
    ArrayList<Image> list = readImage(path + "/" + name + ".gif", size);
    if (list != null) {
      images.put(name, list);
    }
    else {
      for (CellSide direction : CellSide.values()) {
        String key = name + direction.name();
        list = readImage(path + "/" + key + ".gif", size);
        if (list == null) {
          return false;
        }
        images.put(key, list);
      }
    }
    return true;
  }
  
  public ArrayList<Image> readImage(String image, int size) {
    InputStream is = ImageHandler.class.getResourceAsStream(image);
    if (is != null) {
      try {
        ImageInputStream stream = ImageIO.createImageInputStream(is);
        if (stream != null) {
          ArrayList<Image> list = new ArrayList<Image>();
          Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
          if (!readers.hasNext()) {
            throw new RuntimeException("no image reader found");
          }
          ImageReader reader = readers.next();
          reader.setInput(stream);
          int n = reader.getNumImages(true);
          for (int i = 0; i < n; i++) {
            Image current = reader.read(i);
            if (size > 0) {
              current = scaleImage(current, size);
            }
            list.add(current);
          }
          stream.close();
          return list;
        }
      }
      catch (IOException e) {
        
      }
    }
    return null;
  }
  
  public Image scaleImage(Image image, int size) {
    return image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
  }
  
  public Image getImage(String name, int frame) {
    ArrayList<Image> list = images.get(name);
    return list.get(frame % list.size());
  }
  
  public Image getImage(String name) {
    return images.get(name).get(0);
  }
}
