package test;

import core.Board;
import core.Connect4;
import core.Connect4ComputerPlayer;
import core.Player;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static core.Symbol.O;
import static core.Symbol.X;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Connect4Test {
    private Connect4 game = new Connect4();
    private Player x = new Player(X);
    private Player o = new Player(O);

    @Test
    public void testPlay(){
        game.onlineProperty().set(true);
        game.opponentProperty().set("P");
        game.playGame();
        game.onlineProperty().set(false);
        game.opponentProperty().set("C");
        game.playGame();
        game.moveProperty().set(1);
        game.playTest();
    }

    @Test
    public void testComputerMove(){
        Connect4ComputerPlayer c = new Connect4ComputerPlayer(X);
        Board b = new Board();
        int move = c.move(b);
        boolean inBound = false;
        if(move>=0||move<b.getCols()) inBound = true;
        System.out.print(b);
        assertTrue("Computer Move Test: PASS",inBound);
    }

    @Test
    public void testHorizontalWin(){
        Board b = new Board();
        for(int i = 0; i < 4; i++){
            b.update(x,i);
        }
        System.out.print(b);
        game.setBoard(b);
        assertTrue("Horizontal Win Test: PASS",game.checkHorizontalWin(b));
    }

    @Test
    public void testVerticalWin(){
        Board b = new Board();
        for(int i = 0; i < 4; i++){
            b.update(x,0);
        }
        System.out.print(b);
        game.setBoard(b);
        assertTrue("Vertical Win Test: PASS",game.checkVerticalWin(b));
    }

    @Test
    public void testDiagonalWin(){
        Board b = new Board();
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < i; j++){
                b.update(o,i);
            }
            b.update(x,i);
        }
        System.out.print(b);
        game.setBoard(b);
        assertTrue("Diagonal Win Test: PASS", game.checkDiagonalWin(b));
    }

    @Test
    public void testCheckWin(){
        Board b = new Board();
        assertEquals("Check Win Test - None: PASS", game.checkWin(), -1);
        testHorizontalWin();
        assertEquals("Check Win Test - Horizontal: PASS", game.checkWin(), 0);
        testVerticalWin();
        assertEquals("Check Win Test - Vertical: PASS", game.checkWin(), 1);
        testDiagonalWin();
        assertEquals("Check Win Test - Diagonal: PASS", game.checkWin(), 2);
    }

    @Test
    public void testCheckDraw(){
        Board b = new Board();
        for(int i = 0; i < b.getRows(); i++){
            for(int j = 0; j < b.getCols(); j++){
                b.update(x,j);
            }
        }
        System.out.print(b);
        assertTrue("Draw Test: PASS",game.checkDraw(b));
    }

    @Test
    public void testColIsFull(){
        Board b = new Board();
        for(int i = 0; i < b.getRows(); i++){
            b.update(x,0);
        }
        System.out.print(b);
        assertTrue("Full Column Test: PASS",b.colIsFull(0));
    }
}
