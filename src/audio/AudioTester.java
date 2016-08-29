package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.io.IOException;

/**
 * Created by one on 7/31/16.
 */
public class AudioTester {

    public static void main(String[] args) {
        AudioMaster.init();
        AudioMaster.setListenerData(0, 0, 0);

        int buffer = AudioMaster.loadSound("/audios/bounce.wav");
        Source source = new Source();
        source.setLooping(true);
        source.play(buffer);

        float xPos = 0;
        source.setPosition(xPos, 0, 0);
        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);


        char c = ' ';
        while (c != 'q') {
            try {
                c = (char)System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (c == 'p') {
                if (source.isPlaying()) {
                    source.pause();
                } else {
                    source.continuePlaying();
                }
            }

            xPos -= 0.03f;

            source.setPosition(xPos, 0, 0);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        source.delete();
        AudioMaster.cleanUp();

    }
}
