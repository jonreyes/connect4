package core;

import ui.Connect4TextConsole;

/**
 * Class
 * Thread for running Game.
 */
public class GameThread extends Thread {
    Connect4TextConsole game = new Connect4TextConsole();
    public Connect4 getGame(){
        return game.getGame();
    }
}
