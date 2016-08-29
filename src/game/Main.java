package game;

import engineTester.Game;
import engineTester.GameEngine;
import engineTester.GameLogic;

/**
 * Created by one on 8/5/16.
 */
public class Main {

    public static void main(String[] args) {

        try {
            boolean vSync = true;
            GameLogic gameLogic = new Game();
            GameEngine gameEng = new GameEngine("GAME", 1280, 710, vSync, gameLogic);
            gameEng.start();
            gameEng.cleanUp();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }

    }

}
