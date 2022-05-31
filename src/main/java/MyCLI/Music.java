package MyCLI;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music {
    private AudioInputStream audioStream;
    private Clip clip;
    private File file;
    private String filename = "Kiki's Delivery Service - Umi no Mieru Machi.wav";

    public Music() {
        // check if audio file exists or not
        file = new File("./assets/music/" + filename);
        if (!file.exists()) {
            System.out.println("Audio file was missing!");
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        }
        catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file format not supported!");
        }
        catch (LineUnavailableException e) {
            System.out.println("Line is unavailable at this moment!");
        }
        catch (IOException e) {
            System.out.println("Problem with file input");
        }
    }

    public void play() {
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    public void close() {
        clip.close();
    }
}

