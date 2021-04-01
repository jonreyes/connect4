package core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import static core.Symbol.*;

/**
 * Class
 * Defines a template for the Board object within the context of the Connect4 Game.
 * In Connect4, the game Board has 6 rows and 7 columns. A total of 42 windows.
 * @author Jon Reyes
 */
public class Board{
    private final int rows = 6;
    private final int cols = 7;
    private SimpleObjectProperty<Color>[][] colors = new SimpleObjectProperty[rows][cols];
    private SimpleObjectProperty<Symbol>[][] pieces = new SimpleObjectProperty[rows][cols];
    private Symbol[][] windows = new Symbol[rows][cols];
    public Board(){
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++){
                colors[i][j] = new SimpleObjectProperty<Color>(Color.WHITE);
                if(i<this.rows) pieces[i][j] = new SimpleObjectProperty<Symbol>();
            }
        }
    }
    public Symbol getWindow(int r, int c){
        return windows[r][c];
    }
    public void setWindow(int r, int c, Symbol s){
        windows[r][c] = s;
    }
    public int getRows(){
        return this.rows;
    }
    public int getCols(){
        return this.cols;
    }
    public String getRow(int r){
        StringBuilder s = new StringBuilder();
        for(int c = 0; c < cols; c++){
            Symbol w = windows[r][c];
            s.append((w!=null)?w:" ");
        }
        return s.toString();
    }

    public String getCol(int c){
        StringBuilder s = new StringBuilder();
        for(int r = 0; r < rows; r++){
            Symbol w = windows[r][c];
            s.append((w!=null)?w:" ");
        }
        return s.toString();
    }

    public boolean colIsFull(int c){
        //System.out.println(this.getCol(c));
        return this.getCol(c).trim().length()==this.rows;
    }

    /**
     * Updates the Board when a Player makes a move.
     * @param p The active Player is required for placing the appropriate game piece signature within the Board.
     * @param move The column selected for the Player's move is required when dropping a game piece into the Board.
     * */
    public void update(Player p, int move){
        int col = move;
        for(int i = rows-1; i >=0; i--){
            Symbol w = windows[i][col];
            if(w==null) {
                windows[i][col] = p.getSymbol();
                pieces[i][col].set(p.getSymbol());
                switch (p.getSymbol()){
                    case X: colors[i][col].set(Color.RED); break;
                    case O: colors[i][col].set(Color.YELLOW); break;
                    default: break;
                }
                break;
            }
        }
    }

    /** Determines the expected window a piece will fall into given a move.
     * @param p Corresponding Player making expected move.
     * @param move The column of the expected move.
     * @return The expected window
     * */
    public int[] drop(Player p, int move){
        int r = -1;
        int col = move;
        for(int i = rows-1; i >=0; i--){
            Symbol w = windows[i][col];
            if(w==null) {
                r = i;
                break;
            }
        }
        return new int[]{r,col};
    }

    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                s.append("|");
                Symbol w = windows[i][j];
                s.append((w!=null)?w:" "); //String.format("%d%d",i,j));
            }
            s.append("|\n");
        }
        return s.toString();
    }

    public Board clone(){
        Board c = new Board();
        for(int i = 0; i < this.rows; i++){
            for (int j = 0; j < this.cols; j++){
                c.setWindow(i,j,this.windows[i][j]);
            }
        }
        return c;
    }

    public SimpleObjectProperty<Color> colorProperty(int i, int j) {
        return colors[i][j];
    }
}
