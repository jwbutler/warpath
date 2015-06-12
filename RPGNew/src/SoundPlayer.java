import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
  public SoundPlayer() {
  }
  public void playSoundThread(final String filename) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        // TODO Auto-generated method stub
        playSound(filename);
      }
    }).run();
  }
  public void playSound(String filename) {
    try {
      final Clip clip = AudioSystem.getClip();
      final AudioInputStream stream = AudioSystem.getAudioInputStream(new File("sounds/"+filename));
      clip.open(stream);
      clip.loop(0);
      clip.addLineListener(new LineListener() {

        @Override
        public void update(LineEvent e) {
          if (e.getType()==LineEvent.Type.STOP) {
            clip.close();
            try {
              stream.close();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
          
        }
      });
    } catch (LineUnavailableException | IOException
        | UnsupportedAudioFileException e) {
      e.printStackTrace();
    }
  }
}
