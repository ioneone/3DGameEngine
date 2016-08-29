package engineTester;

import entities.Camera;
import renderEngine.Window;
import mouse.MouseInput;

/**
 * Created by one on 7/24/16.
 */
public interface GameLogic {

    void init(Window window) throws Exception;

    void input(Window window);

    void update(float interval, MouseInput mouseInput, Window window);

    void render(Window window);

    void cleanUp();

}
