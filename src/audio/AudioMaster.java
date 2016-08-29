package audio;

/**
 * Created by one on 7/31/16.
 */

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.*;

public class AudioMaster {

    private static long device = NULL;
    private static long context = NULL;

    private static List<Integer> buffers = new ArrayList<Integer>(); // CD

    public static void init() {

        device = alcOpenDevice((ByteBuffer)null);
        if ( device == NULL ) {
            throw new IllegalStateException("Failed to open the default device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer)null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

    }

    public static void setListenerData(float x, float y, float z) {

        AL10.alListener3f(AL10.AL_POSITION, x, y, z); // position
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0); // velocity

    }

    public static int loadSound(String filePath) {

        int bufferID = AL10.alGenBuffers();
        buffers.add(bufferID);

        WaveData waveFile = WaveData.create(filePath);
        // store data from datafile into buffer
        AL10.alBufferData(bufferID, waveFile.format, waveFile.data, waveFile.samplerate);

        waveFile.dispose();
        return bufferID;


    }

    public static void cleanUp() {
        for (int buffer:buffers) {
            AL10.alDeleteBuffers(buffer);
        }
        alcCloseDevice(device);
        alcDestroyContext(context);

    }

}
