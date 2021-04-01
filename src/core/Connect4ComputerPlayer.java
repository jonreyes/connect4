package core;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class
 * Defines the template for the Connect4 CPU/AI Object.
 * Extension that inherits from the Player class.
 * */
public class Connect4ComputerPlayer extends Player {
    /** The number of moves the CPU has made*/
    private int moves = 0;
    /** The window position of the last key move the CPU has made.*/
    private int[] last;
    /** Creates the Connect 4 Computer Player object.
     * @param s A Symbol X or O is a relevant signature to identify Player or game piece.*/
    public Connect4ComputerPlayer(Symbol s){
        super(s);
    }
    /** The function that handles AI move generation logic.
     *  @param b Accepts a Board to evaluate and decide next move.
     *  @return The column of the determined move.*/
    public int move(Board b){
        System.out.println("Random Column Selected.");
        Random r = new Random();
        int col = r.nextInt(b.getCols());
        last = b.drop(this, col);
        if(last[0]==-1) last = new int[]{0,col};
        if(predictWin(b)>=0) {
            col = predictWin(b);
            System.out.printf("Predicted a Win at Col %d.\n",col+1);
        } else if(last!=null) {
            int[][][] neighbors = new int[][][]{
                    {
                            {last[0] - 1, last[1] - 1}, {last[0] - 1, last[1]}, {last[0] - 1, last[1] + 1}
                    },
                    {
                            {last[0], last[1] - 1}, {last[0], last[1] + 1}
                    },
                    {
                            {last[0] + 1, last[1] - 1}, {last[0] + 1, last[1]}, {last[0] + 1, last[1] + 1}
                    }
            };
            col = last[1];
            ArrayList<Integer> validNeighbors = new ArrayList<>();
            for (int[][] n : neighbors) {
                for (int[] e : n) {
                    // if x is valid Column
                    if (e[1] >= 0 && e[1] < b.getCols()) {
                        // get drop window
                        int[] drop = b.drop(this, e[1]);
                        if (drop[0] == e[0] && drop[1] == e[1]) {
                            validNeighbors.add(e[1]);
                        }
                    }
                }
            }
            if(validNeighbors.size()>0) {
                System.out.println("Neighbor Selected.");
                col = validNeighbors.get(r.nextInt(validNeighbors.size()));
            }
            last = b.drop(this, col);
            //System.out.printf("%d %d %d\n",last[0],last[1],col);
            if(last[0]==-1) last = new int[]{0,col};
        }
        b.update(this,col);
        moves++;
        return col;
    }

    /** Helper function to determine if the Player or Opponent can make a potential winning move.
     * @param b Accepts a Board to evaluate possible wins.
     * @return The column of a potential win.*/
    public int predictWin(Board b){
        Connect4 c = new Connect4(false);
        Player opponent = new Player((this.getSymbol()==Symbol.X)?Symbol.O:Symbol.X);
        for(int i = 0; i < b.getCols(); i++) {
            Board t = b.clone();
            t.update(this,i);
            c.setBoard(t);
            if(c.checkWin()!=-1) {
                return i;
            }
            t = b.clone();
            t.update(opponent,i);
            c.setBoard(t);
            if(c.checkWin()!=-1){
                return i;
            }
            //System.out.println(t);
        }
        return -1;
    }
}
