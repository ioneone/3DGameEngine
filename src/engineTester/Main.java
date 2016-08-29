package engineTester;


/**
 * Created by one on 7/23/16.
 */
public class Main {

    public static void main(String[] args) {

        try {
            boolean vSync = true;
            GameLogic gameLogic = new Game();
            GameEngine gameEng = new GameEngine("GAME", 720, 480, vSync, gameLogic);
            gameEng.start();
            gameEng.cleanUp();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }

    }

}
