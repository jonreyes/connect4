package core;

import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * Class
 * Defines a template for the Player object within the context of the Connect4 Game.
 * Players must be associated with a signature identifying their game piece.
 * @author Jon Reyes
 * */
public class Player {
    /** The symbol is a signature identifying a Player and their game piece.*/
    private Symbol symbol;

    /** Constructor
     *  Creates the Player object.
     *  @param s A Symbol X or O is a relevant signature for a Player and their game piece.*/
    public Player(Symbol s){
        this.symbol = s;
    }
    /** Method for accessing a Player's signature. */
    public Symbol getSymbol(){
        return this.symbol;
    }
    /** Determines a Player's move by prompting them
     *  to select a column to drop a game piece.
     *  Validates input for invalid moves.
     * @return The column selected by the Player as an array index.
     * */
    public int move(Board b){
        String colprompt = "Choose a column number from 1-7.";
        System.out.println(colprompt);
        int col = 0;
        while(col==0){
            try{
                Scanner s = new Scanner(System.in);
                int t = s.nextInt();
                if(!(t>=1&&t<=7)) throw new InputMismatchException();
                if(b.colIsFull(t-1)) throw new FullColumnException();
                col = t;
            } catch (InputMismatchException e) {
                System.out.println("Invalid Move.");
                System.out.println(colprompt);
            } catch (FullColumnException e){
                System.out.println("Invalid Move.");
                System.out.println(e.getMessage());
                System.out.println(colprompt);
            }
        }
        return col-1;
    }
    public String toString(){
        String s = (symbol.equals(Symbol.X))?"X":"O";
        return String.format("Player %s",s);
    }
}
