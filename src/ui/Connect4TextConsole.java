package ui;

import core.Board;
import core.Connect4;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Scanner;

/**
 * Class
 * Defines the Connect4 Text Console UI Object.
 * @author Jon Reyes
 * */
public class Connect4TextConsole {
    private Connect4 game;
    private SimpleObjectProperty<Connect4> gameProp = new SimpleObjectProperty<Connect4>();
    public Connect4TextConsole() {
        this.game = new Connect4();
        this.gameProp.set(this.game);
        this.game.resultProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t) {
                System.out.println("Would you like to play again? Y/N");
                Scanner in = new Scanner(System.in);
                String p = in.next().toUpperCase();
                if (p.equals("Y")) {
                    game = new Connect4();
                    gameProp.set(game);
                }
            }
        });
    }
    /** Attribute accessor for Connect4 Game logic object*/
    public Connect4 getGame(){
        return this.game;
    }

    /** Attribute property accessor for Connect4 Game object*/
    public SimpleObjectProperty<Connect4> gameProperty(){
        return this.gameProp;
    }
}
