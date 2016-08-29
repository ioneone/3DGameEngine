package toolBox;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by one on 7/23/16.
 */

public class Timer {

    private static double lastLoopTime;

    public static void init() {
        lastLoopTime = getTime();
    }

    // This timer is somehow not accurate
    /*
    public static double getTime() {
        return (double)System.nanoTime() / (double)1000_000_000.0;
    }
    */

    public static double getTime() {
        return glfwGetTime();
    }

    public static float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }


    public static double getLastLoopTime() {
        return lastLoopTime;
    }

    public static float getDelta() {
        return getElapsedTime() * 3000;
    }


}