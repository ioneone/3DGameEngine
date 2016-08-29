package engineTester;

import mouse.MouseInput;
import mouse.MousePicker;
import toolBox.Timer;
import renderEngine.Window;

/**
 * Created by one on 7/24/16.
 */
public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;

    private final MouseInput mouseInput;

    private final Window window;

    private final Thread gameLoopThread;

    private final Timer timer;

    private final GameLogic gameLogic;

    public GameEngine(String windowTitle, int width, int height, boolean vSync, GameLogic gameLogic) throws Exception {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync);
        this.gameLogic = gameLogic;
        timer = new Timer();
        mouseInput = new MouseInput();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        }
    }

    protected void init() throws Exception {
        window.init();
        timer.init();

        mouseInput.init(window);
        gameLogic.init(window);
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();


            if (!window.isvSync()) { // vertical synchronization
                sync();
            }
        }
    }

    // not to exhaust computer resources by rendering continuously
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window);

    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput, window);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }


    public void cleanUp() {
        window.cleanUp();
        gameLogic.cleanUp();
    }


}
