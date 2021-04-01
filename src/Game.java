import core.Connect4;
import core.Connect4Client;
import core.Connect4Server;
import core.GameThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ui.Connect4GUI;
import ui.Connect4TextConsole;

import java.util.Scanner;

/**
 *  Game
 *  The class for instantiating an appropriate UI
 *  and running the Connect4 Game.
 *  @author Jon Reyes
 */
public class Game {
    public static void main(String[] args){
        try {
            System.out.println("Welcome to Connect 4");
            Scanner in = new Scanner(System.in);
            System.out.println("Enter 'C' to use Console-Based Text UI.");
            System.out.println("Otherwise, game will be started in GUI mode.");
            String ui = in.next();
            ui = ui.toUpperCase();
            if ("C".equals(ui)) {
                System.out.println("Text Console UI selected.");
                GameThread t = new GameThread();
                t.start();
            } else {
                System.out.println("GUI selected.");
                Application.launch(Connect4GUI.class, args);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
