package warpath.ui;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/** Used to play sounds.
 * TODO Music */
public class SoundPlayer {
  private final String SOUNDS_FOLDER = "sounds";
  public SoundPlayer() {
  }
  public void playSoundThread(final String filename) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        playSound(filename);
      }
    }).run();
  }
  public void playSound(String filename) {
    try {
      final Clip clip = AudioSystem.getClip();
      final AudioInputStream stream = AudioSystem.getAudioInputStream(new File(SOUNDS_FOLDER+File.separatorChar+filename));
      clip.open(stream);
      // Play the file once to completion.
      clip.loop(0);
      
      // This listener is used to close the stream at the end of input.
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
