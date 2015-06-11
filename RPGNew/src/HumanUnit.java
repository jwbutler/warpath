import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import jwbgl.*;
/* This class is used to represent humanoid units.  Particularly the ones
 * that use Will's original "player" sprite.  It'll be subclassed lots. */

public class HumanUnit extends Unit implements Serializable {
  private static String[] defaultActivities = {"walking", "standing", "attacking"};
  
  public HumanUnit(RPG game, String name, String[] activities, Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, new HashMap<Color, Color>(), posn, player);
    this.setyOffset(-32);
  }  
  
  public HumanUnit(RPG game, String name, String[] activities, HashMap<Color, Color> paletteSwaps,
    Posn posn, Player player) {
    //super(game, name, "player", activities, posn, player);
    this(game, name, "player", activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }  
  
  public HumanUnit(RPG game, String name, Posn posn, Player player) {
    this(game, name, defaultActivities, posn, player);
    this.setyOffset(-32);
  }
  
  public HumanUnit(RPG game, String name, String animationName,
  String[] activities, HashMap<Color, Color> paletteSwaps, Posn posn, Player player) {
    super(game, name, animationName, activities, paletteSwaps, posn, player);
    this.setyOffset(-32);
  }

  @Override
  public void playHitSound() {
    // TODO Auto-generated method stub
    try {
      AudioInputStream stream = javax.sound.sampled.AudioSystem.getAudioInputStream(new File("sounds/hit1.wav"));
      DataLine.Info info  = new DataLine.Info(Clip.class, stream.getFormat());
      final Clip clip = (Clip)javax.sound.sampled.AudioSystem.getLine(info);
      clip.addLineListener(new LineListener() {

        @Override
        public void update(LineEvent e) {
          // TODO Auto-generated method stub
          if (e.getType().equals(LineEvent.Type.STOP))
          {
            clip.close();
          }
          else if (e.getType().equals(LineEvent.Type.CLOSE))
          {
            System.exit(0);
          }
        }
      });
      clip.loop(1);
      
    } catch (UnsupportedAudioFileException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (LineUnavailableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  @Override
  public void setCurrentActivity(String newActivity) {
    currentActivity = newActivity;
    setCurrentAnimation(newActivity, getCurrentDirection());
  }
}
