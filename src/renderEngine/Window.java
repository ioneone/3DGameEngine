package renderEngine;

/**
 * Created by one on 7/23/16.
 */

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import shaders.StaticShader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private final String title;

    private static int width;

    private static int height;

    private static long windowHandle;

    private GLFWErrorCallback errorCallback;

    private GLFWKeyCallback keyCallback;

    //private GLFWWindowSizeCallback windowSizeCallback;

    private GLFWFramebufferSizeCallback framebufferSizeCallback;

    private boolean resized;

    private boolean vSync;

    public static double windowCoordinatesPixelRate;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;
    }

    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4); // set up antialiasing

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL); // set the first NULL to glfwGetPrimaryMonitor() to get full-screen window
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }


        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetFramebufferSize(windowHandle, w, h);

        windowCoordinatesPixelRate = w.get(0) / this.width;

        this.width = w.get(0);
        this.height = h.get(0);

        // Setup resize callback
        /*
        glfwSetWindowSizeCallback(windowHandle, windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.width = width;
                Window.this.height = height;
                Window.this.setResized(true);
            }
        });
        */

        glfwSetFramebufferSizeCallback(windowHandle, framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.width = width;
                Window.this.height = height;
                glViewport(0, 0, width, height);
            }
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });

        // set up mouse cursor
        setUpMouseCursor("/textures/red.png", windowHandle);


        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        GL11.glEnable(GL13.GL_MULTISAMPLE);



        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public void cleanUp() {
        try {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(windowHandle);
            glfwDestroyWindow(windowHandle);
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    public static long getWindowHandle() {
        return windowHandle;
    }

    public void setUpMouseCursor(String imagePath, long windowHandle) {

        // Load Texture file
        try {
            PNGDecoder decoder = new PNGDecoder(Window.class.getResourceAsStream(imagePath));

            // Load texture contents into a byte buffer
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();

            // create a GLFWImage
            GLFWImage cursorImg= GLFWImage.malloc().set(16, 16, buf);


            // create custom cursor and store its ID
            int hotspotX = 0;
            int hotspotY = 0;
            long cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX , hotspotY);

            // set current cursor
            glfwSetCursor(windowHandle, cursorID);

        } catch (Exception e) {
            System.out.println("Failed to load " + imagePath);
            e.printStackTrace();
        }


    }
}
