package core;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import static core.Symbol.*;

/**
 * Class
 * Object template for Game client.
 * @author Jon Reyes
 */
public class Connect4Client {
    private String host = "localhost";
    private Socket socket;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private SimpleIntegerProperty moveProp = new SimpleIntegerProperty(-1);
    private SimpleStringProperty symbolProp = new SimpleStringProperty();
    private Symbol symbol;

    public Connect4Client(){
        connect();
    }

    private void connect(){
        try {
            System.out.println("Connecting to Server...");
            // Create a socket to connect to the server
            socket = new Socket(host, 8000);
            System.out.println("Connected to Server.");
            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());
            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        }catch (Exception e) {
            System.out.println("Failed to connect to the Server.");
            e.printStackTrace();
        }
        Thread client = new Thread(){
            public void run(){
                try {
                    System.out.println("Waiting for Players...");
                    int ready = fromServer.readInt();
                    System.out.println("Checking if Ready...");
                    if (ready == 1) {
                        System.out.println("Ready!");
                        symbol = readSymbol();
                        symbolProp.set(symbol.toString());
                        System.out.printf("You are Player %s.\n",symbol);
                        while (true) {
                            moveProp.set(getMove());
                            moveProp.set(-1);
                        }
                    }
                } catch(SocketException e){
                    System.out.println("Client Disconnected.");
                } catch(IOException e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        client.start();
    }

    public void sendMove(int col){
        try{
            System.out.printf("Sending Player %s's Move to Server...\n",symbol);
            toServer.writeInt(col);
            System.out.println("Sent Move to Server.");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getMove() throws IOException, SocketException{
        Symbol active = symbol.equals(X)?O:X;
        int move = fromServer.readInt();
        System.out.printf("Received Player %s's Move.\n",active);
        return move;

    }

    public Symbol readSymbol() throws IOException, SocketException{
        return ((fromServer.readInt()==0)? X: O);
    }

    public Symbol getSymbol(){
        return this.symbol;
    }

    public SimpleIntegerProperty moveProperty(){
        return this.moveProp;
    }

    public SimpleStringProperty symbolProperty(){
        return this.symbolProp;
    }

    public void close(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
