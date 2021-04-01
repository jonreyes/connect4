package core;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import static core.Symbol.X;
import static core.Symbol.O;
/**
 *  Connect4
 *  The class responsible for the main game logic
 *  of Connect4.
 * @author Jon Reyes
 * */
public class Connect4 {
    /** Static constants for determining win conditions. */
    static final String xWin = "XXXX";
    static final String oWin = "OOOO";
    /** The Game Board */
    private Board b = new Board();
    /** Player X */
    private Player one = new Player(X);
    /** Player O */
    private Player two = new Player(O);
    private Connect4ComputerPlayer cput = new Connect4ComputerPlayer(X);
    private Connect4ComputerPlayer cpu = new Connect4ComputerPlayer(O);
    /** The current turn belongs to the active player */
    private Player active;
    /** The winning player of the Game*/
    private Player winner;
    private int[] score = new int[2];
    /** The type of opponent to play against
     *  P   Player
     *  C   Computer
     * */
    private String opponent;
    private SimpleObjectProperty<Player> activeProp = new SimpleObjectProperty<Player>();
    private SimpleObjectProperty<Player> winProp = new SimpleObjectProperty<Player>();
    private SimpleStringProperty oppProp = new SimpleStringProperty();
    private SimpleIntegerProperty moveProp = new SimpleIntegerProperty(-1);
    private SimpleIntegerProperty turnProp = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty statProp = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty[] scoreProp = new SimpleIntegerProperty[2];
    private SimpleStringProperty resProp = new SimpleStringProperty();
    private SimpleBooleanProperty onlineProp = new SimpleBooleanProperty(false);
    private SimpleStringProperty idProp = new SimpleStringProperty();
    /**
     * The status of the Game.
     * 0 for PLAY
     * 1 for WIN
     * 2 for DRAW
     * */
    private int status = 0;
    /** The number of moves/turns played in the game.*/
    private int turns = 0;
    private Connect4Client client;

    public Connect4(){
        scoreProp[0] = new SimpleIntegerProperty(score[0]);
        scoreProp[1] = new SimpleIntegerProperty(score[1]);
        scoreProp[0].addListener(e->{
            score[0] = scoreProp[0].get();
        });
        scoreProp[1].addListener(e->{
            score[1] = scoreProp[1].get();
        });
        oppProp.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String o) {
                opponent = o;
                System.out.printf("%s Selected.\n",o);
                if(o.equals("P")&&!onlineProp.get()){
                    System.out.println("Enter 'O' if you want to play online.");
                    System.out.println("Otherwise, the game will be played locally.");
                    Scanner in = new Scanner(System.in);
                    String t = in.next().toUpperCase();
                    if(t.equals("O")){
                        System.out.println("Playing Online.");
                        onlineProperty().set(true);
                    } else {
                        System.out.println("Playing Locally.");
                        onlineProperty().set(false);
                    }
                }
            }
        });
        activeProp.addListener(new ChangeListener<Player>() {
            @Override
            public void changed(ObservableValue<? extends Player> observableValue, Player player, Player a) {
                // Show Player Turn
                active = a;
                if(oppProp.get().equals("P")) System.out.printf("%s - your turn.\n",active);
                if(oppProp.get().equals("C")) System.out.printf("It is %s turn.\n",(active!=cpu)?"your":"the Computer's");
                if(onlineProp.get()&&!active.getSymbol().equals(client.getSymbol())) System.out.printf("Waiting for %s's Move...\n",active);
            }
        });
        onlineProp.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean original, Boolean change) {
                if(change){
                    client = new Connect4Client();
                    client.symbolProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observableValue, String s, String t) {
                            idProp.set(t);
                            playGame();
                        }
                    });
                    client.moveProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observableValue, Number n, Number m) {
                            int col = m.intValue();
                            moveProp.set(col);
                        }
                    });
                }
            }
        });
        statProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(statProp.get()>0){
                    String result = "";
                    if(oppProp.get()!=null) {
                        switch (statProp.get()) {
                            case 1:
                                winProp.set(winner);
                                if (!oppProp.get().equals("C")) {
                                    if (client == null) {
                                        result = String.format("%s Won.", winner);
                                    } else {
                                        result = (winner.getSymbol() == client.getSymbol()) ? "You Won." : "You Lost.";
                                    }
                                }
                                if (oppProp.get().equals("C")) {
                                    result = String.format("%s Won.", (winner == cpu) ? "CPU" : "You");
                                }
                                break;
                            case 2:
                                result = "Draw. Game Over.";
                                break;
                            default:
                                break;
                        }
                    }
                    System.out.println(result);
                    resProp.set(result);
                }
            }
        });
        moveProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number n, Number m) {
                Thread moveThread = new Thread() {
                    public void run() {
                        try {
                            if (onlineProp.get()) {
                                int col = m.intValue();
                                if(status==0){
                                    if (col >= 0 && col < b.getCols()) {
                                        System.out.printf("Column %d Selected.\n", col + 1);
                                        if (b.colIsFull(col)) throw new FullColumnException();
                                        b.update(active, col);
                                        // Display Board
                                        System.out.print(b);
                                        if (client.getSymbol().equals(active.getSymbol())) {
                                            client.sendMove(col);
                                        }
                                        // Check Game Over
                                        status = checkGameOver(b);
                                        statProp.set(status);
                                        if (status == 0) {
                                            turns++;
                                            turnProp.set(turns);
                                        }
                                    }
                                }
                                playOnline();
                            } else {
                                int i = (!oppProp.get().equals("P")) ? 1 : 0;
                                while (i >= 0) {
                                    int col = m.intValue();
                                    if (status == 0 && col >= 0 && col < b.getCols()) {
                                        // Update Board
                                        col = (oppProp.get().equals("C") && active == cpu) ? active.move(b.clone()) : m.intValue();
                                        if (b.colIsFull(col)) {
                                            if (active == one) throw new FullColumnException();
                                            if (active == cpu || active == cput) col = active.move(b.clone());
                                        }
                                        System.out.printf("Column %d Selected.\n", col + 1);
                                        b.update(activeProp.get(), col);
                                        // Display Board
                                        System.out.print(b);
                                        // Check Game Over
                                        status = checkGameOver(b);
                                        statProp.set(status);
                                        if (status == 0) {
                                            turns++;
                                            turnProp.set(turns);
                                        }
                                    }
                                    i--;
                                }
                            }
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        } catch (FullColumnException e){
                            System.out.println(e.getMessage());
                        }
                    }
                };
                moveThread.start();
            }
        });
        turnProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number n, Number m) {
                turns = m.intValue();
                System.out.println("Turn "+turns);
                active = (turns %2==1)?one:two;
                if(oppProp.get().equals("T")) active = (turns %2==1)?cput:cpu;
                activeProp.set(active);
            }
        });
        Thread t = new Thread(){
            public void run(){
                if(!onlineProp.get()) {
                    opponent = mode();
                }
            }
        };
        t.start();
    }

    public Connect4(boolean play){
        scoreProp[0] = new SimpleIntegerProperty(score[0]);
        scoreProp[1] = new SimpleIntegerProperty(score[1]);
        scoreProp[0].addListener(e->{
            score[0] = scoreProp[0].get();
        });
        scoreProp[1].addListener(e->{
            score[1] = scoreProp[1].get();
        });
    }

    /** The function to determined the mode of the game based on the type of opponent*/
    public String mode(){
        System.out.println("Enter 'P' if you want to play against another player.");
        System.out.println("Enter 'C' if you want to play against the computer.");
        String o = oppProp.get();
        while(null==o){
            try{
                Scanner in = new Scanner(System.in);
                String t = in.next().toUpperCase();
                if(!(t.equals("P")||t.equals("C"))) throw new InputMismatchException();
                o = t;
            } catch (InputMismatchException e){
                System.out.println("Invalid Input.");
                System.out.println("Enter 'P' if you want to play against another player.");
                System.out.println("Enter 'C' if you want to play against the computer.");
            } catch (Exception e){
                System.out.println(e);
            }
        }
        oppProp.set(o);
        return o;
    }
    /** The function to play the game holding the main game logic in a GUI*/
    public void playGame(){
        Board b = new Board();
        System.out.print(b.toString());
        System.out.println("Begin Game.");
        if("C".equals(opponent)){
            System.out.println("Start game against computer.");
            two = cpu;
        }
        Thread play = new Thread(){
            public void run(){
                turns++;
                turnProp.set(turns);
                switch (oppProp.get()){
                    case "P":
                        if(!onlineProp.get()){
                            playLocal();
                        } else {
                            playOnline();
                        }
                        break;
                    case "C": playLocal(); break;
                    case "T": playTest(); break;
                    default: break;
                }
            }
        };
        play.start();
    }
    /** The function to play the game holding the main game logic in the console */
    public void playLocal(){
        try {
            while (status==0) {
                    // Update Board
                    int col = active.move(b.clone());
                    if (b.colIsFull(col)) {
                        if(active==one) throw new FullColumnException();
                        if(active==cpu||active==cput) col = active.move(b.clone());
                    }
                    System.out.printf("Column %d Selected.\n", col + 1);
                    b.update(activeProp.get(), col);
                    // Display Board
                    System.out.print(b);
                    // Check Game Over
                    status = checkGameOver(b);
                    statProp.set(status);
                    if (status == 0) {
                        turns++;
                        turnProp.set(turns);
                    }
                }
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (FullColumnException e){
            System.out.println(e.getMessage());
        }
    }

    private void playOnline(){
        try {
            int col = -1;
            if (status == 0){
                if(active.getSymbol().equals(client.getSymbol())) col = active.move(b.clone());
                if (col >= 0 && col < b.getCols()) {
                    System.out.printf("Column %d Selected.\n", col + 1);
                    if (b.colIsFull(col)) throw new FullColumnException();
                    b.update(activeProp.get(), col);
                    if(active.getSymbol().equals(client.getSymbol())){
                        client.sendMove(col);
                    }
                    System.out.print(b);
                    status = checkGameOver(b);
                    statProp.set(status);
                    if (status == 0) {
                        turns++;
                        turnProp.set(turns);
                    }
                }
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (FullColumnException e){
            System.out.println(e.getMessage());
        }
    }

    public void playTest(){
        try {
            while (status == 0) {
                System.out.println("Turn "+turns);
                // Update Board
                int col = active.move(b.clone());
                if (b.colIsFull(col)) {
                    if (active == cpu || active == cput) col = active.move(b.clone());
                }
                System.out.printf("Column %d Selected.\n", col + 1);
                Thread.sleep(250);
                b.update(activeProp.get(), col);
                // Display Board
                System.out.print(b);
                // Check Game Over
                status = checkGameOver(b);
                statProp.set(status);
                if (status == 0) {
                    turns++;
                    turnProp.set(turns);
                }
            }
        } catch (NullPointerException e){
            System.out.println(e.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /** Determines the status of the game
     * @param b The board of the game must be provided for evaluation.
     * @return The evaluation result will become the status of the game.
     *          0 for PLAY
     *          1 for WIN
     *          2 for DRAW
     * */
    public int checkGameOver(Board b){
        int gameOver = 0;
        boolean hw = checkHorizontalWin(b);
        boolean vw = checkVerticalWin(b);
        boolean dw = checkDiagonalWin(b);
        if(hw||vw||dw) {
            gameOver = 1;
            statProp.set(1);
        }
        boolean d = checkDraw(b);
        if(d) {
            gameOver = 2;
            statProp.set(2);
        }
        return gameOver;
    }

    /** Determines a player has won in the horizontal fashion.
     * @param b The board of the game must be provided for evaluation.
     * @return true if a win condition exists false otherwise.
     * */
    public boolean checkHorizontalWin(Board b){
        for(int i = 0; i < b.getRows(); i++){
            String row = b.getRow(i).trim();
            //if(!row.isBlank()) System.out.printf("%d %s\n",i+1,row);
            if(checkWin(row)) return true;
        }
        return false;
    }

    /** Determines a player has won in the vertical fashion.
     * @param b The board of the game must be provided for evaluation.
     * @return true if a win condition exists false otherwise.
     * */
    public boolean checkVerticalWin(Board b){
        for(int j = 0; j < b.getCols(); j++){
            String col = b.getCol(j).trim();
            //if(!col.isBlank()) System.out.printf("%d %s\n",j+1,col);
            if(checkWin(col)) return true;
        }
        return false;
    }

    /** Determines a player has won in the diagonal fashion.
     * @param b The board of the game must be provided for evaluation.
     * @return true if a win condition exists false otherwise.
     * */
    public boolean checkDiagonalWin(Board b){
        boolean win = (checkAscendingWin(b)||checkDescendingWin(b));
        return win;
    }

    /** Helper function dividing the logic of determining a diagonal winner.
     * @param b The board of the game must be provided for evaluation.
     * @return true if a win condition exists false otherwise.
     * */
    private boolean checkAscendingWin(Board b){
        ArrayList<ArrayList<String>> diagonals = new ArrayList<>();
        // Upper Ascending
        for(int i = b.getRows()-1; i >= 0; i--){
            ArrayList<String> d = new ArrayList<>();
            int j = 0;
            while(j<b.getCols()){
                if(i-j>=0) {
                    Symbol w = b.getWindow(i-j, j);
                    String t = (w!=null)?w.name():" ";//String.format("%d%d",i-j,j);
                    d.add(t);
                }
                j++;
            }
            diagonals.add(d);
        }
        // Lower Ascending
        /*
        50 41 32 23 14 05
        51 42 33 24 15 06
        52 43 34 25 16
        53 44 35 26
        */
        for(int i = 0; i < b.getCols(); i++) {
            ArrayList<String> d = new ArrayList<>();
            int j = 0;
            while(j < b.getCols()){
                int r = b.getRows()-1-j;
                if(r>=0&&i+j<b.getCols()) {
                    Symbol w = b.getWindow(r, i+j);
                    String t = (w != null) ? w.name() : " ";//String.format("%d%d", r, i+j);
                    d.add(t);
                }
                j++;
            }
            diagonals.add(d);
        }
        for(ArrayList<String> diag : diagonals){
            if(diag.size()>=4) {
                StringBuilder s = new StringBuilder();
                for (String d : diag) {
                    s.append(d);
                }
                if (checkWin(s.toString())) return true;
            }
        }
        return false;
    }

    /** Helper function dividing the logic of determining a diagonal winner.
     * @param b The board of the game must be provided for evaluation.
     * @return true if a win condition exists false otherwise.
     * */
    private boolean checkDescendingWin(Board b){
        /* 00 01 02 03 04 05 06
           10 11 12 13 14 15 16
           20 21 22 23 24 25 26
           30 31 32 33 34 35 36
           40 41 42 43 44 45 46
           50 51 52 53 54 55 56
        */
        ArrayList<ArrayList<String>> diagonals = new ArrayList<>();
        // Lower Descending
        for(int i = 0; i < b.getRows(); i++){
            ArrayList<String> d = new ArrayList<>();
            int j = 0;
            while(j<b.getRows()){
                if(i+j<b.getRows()) {
                    Symbol w = b.getWindow(i + j, j);
                    String t = (w != null) ? w.name() : " "; //String.format("%d%d", i + j, j);
                    d.add(t);
                }
                j++;
            }
            diagonals.add(d);
        }
        // Upper Descending
        for(int i = 0; i < b.getCols(); i++){
            ArrayList<String> d = new ArrayList<>();
            int j = 0;
            while(j<b.getRows()){
                if(i+j<b.getCols()) {
                    Symbol w = b.getWindow(j, i + j);
                    String t = (w != null) ? w.name() : " "; //String.format("%d%d", j, i + j);
                    d.add(t);
                }
                j++;
            }
            diagonals.add(d);
        }

        for(ArrayList<String> diag : diagonals){
            if(diag.size()>=4) {
                StringBuilder s = new StringBuilder();
                for (String d : diag) {
                    s.append(d);
                }
                if (checkWin(s.toString())) return true;
            }
        }
        return false;
    }

    /** Determines if the game has ended without a winner
     *  with all available pieces consuming
     *  all available windows of the game board
     *  @return if the total number of moves becomes equivalent
     *          to the total number of windows
     *          then all available pieces have been consumed
     *          and there is a draw where the game is over.
     *  */
    public boolean checkDraw(){
        return (turns >=b.getRows()*b.getCols());
    }

    public boolean checkDraw(Board b){
        int n = 0;
        for(int i = 0; i < b.getRows(); i++){
            for(int j = 0; j < b.getCols(); j++){
                if(!b.getWindow(i,j).equals(null)) n++;
            }
        }
        return n>=b.getRows()*b.getCols();
    }
    /** Determines if a win condition has been met
     * and updates the winner of the game.
     * @param s A string holding the data for pieces
     *          in a row, column, or diagonal.
     * @return true if a winner has been found where there
     *          is a substring of four of the same consecutive symbols.
     *          false otherwise.
     * */
    private boolean checkWin(String s){
        boolean win = false;
        //System.out.println(s);
        if(s.contains(xWin)||s.contains(oWin)) win = true;
        if(s.contains(xWin)){
            winner = one;
            if("T".equals(opponent)) winner = cput;
            if(client!=null){
                if(client.getSymbol()==X) {
                    score[0]++;
                } else {
                    score[1]++;
                }
            } else {
                score[0]++;
            }
            scoreProp[0].set(score[0]);
            scoreProp[1].set(score[1]);
            winnerProperty().set(winner);
        }
        if(s.contains(oWin)) {
            winner = two;
            if("T".equals(opponent)) winner = cpu;
            if(client!=null) {
                if(client.getSymbol()==O) {
                    score[0]++;
                } else {
                    score[1]++;
                }
            } else {
                score[1]++;
            }
            scoreProp[0].set(score[0]);
            scoreProp[1].set(score[1]);
            winnerProperty().set(winner);
        }
        return win;
    }

    /** Determines if a win condition has been met.
     * @return The type of win.
     *  -1 No Win
     *  0 Horizontal
     *  1 Vertical
     *  2 Diagonal
     */
    public int checkWin(){
        if(checkHorizontalWin(b)) return 0;
        if(checkVerticalWin(b)) return 1;
        if(checkDiagonalWin(b)) return 2;
        return -1;
    }

    /** Setter function for board attribute. */
    public void setBoard(Board b){
        this.b = b;
    }

    /** Getter function for game board attribute */
    public Board getBoard(){
        return this.b;
    }

    /** Getter function for active Player attribute */
    public Player getActive(){
        return this.active;
    }

    public SimpleObjectProperty<Player> activeProperty(){
        return activeProp;
    }

    public SimpleStringProperty opponentProperty() {
        return oppProp;
    }

    public SimpleIntegerProperty moveProperty(){
        return moveProp;
    }

    public SimpleObjectProperty<Player> winnerProperty(){
        return winProp;
    }

    public SimpleIntegerProperty[] scoreProperty(){
        return scoreProp;
    }

    public SimpleIntegerProperty turnProperty(){
        return turnProp;
    }

    public SimpleIntegerProperty statusProperty(){
        return statProp;
    }

    public SimpleStringProperty resultProperty(){
        return resProp;
    }

    public SimpleBooleanProperty onlineProperty(){ return onlineProp; }

    public SimpleStringProperty idProperty(){return idProp;}

    public Connect4Client getClient(){
        return this.client;
    }

}
