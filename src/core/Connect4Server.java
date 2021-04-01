package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Connect4Server extends Application{
    private TextArea log;
    private Socket player1;
    private Socket player2;
    public static void main(String[] args){
        launch(args);
    };

    @Override
    public void start(Stage primaryStage){
        log = new TextArea();
        Pane pane = new Pane();
        log.prefHeightProperty().bind(primaryStage.heightProperty());
        log.prefWidthProperty().bind(primaryStage.widthProperty());
        pane.getChildren().add(log);
        Scene scene = new Scene(pane,800,600);
        primaryStage.setTitle("Connect 4 Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        connect();
    }

    private void connect(){
        Thread server = new Thread(){
            public void run(){
                try {
                    int port = 8000;
                    ServerSocket serverSocket = new ServerSocket(port);
                    Platform.runLater(() -> {
                        String message = String.format("%s: Server started on port %d\n", new Date().toString(), port);
                        System.out.print(message);
                        log.appendText(message);
                    });
                    Platform.runLater(() -> {
                        String message = String.format("%s: Waiting for players to join the session.\n", new Date().toString());
                        System.out.print(message);
                        log.appendText(message);
                    });
                    while (true) {
                        player1 = serverSocket.accept();
                        Platform.runLater(() -> {
                            String message1 = String.format("%s: Player 1 joined the session.\n", new Date().toString());
                            String message2 = String.format("Player 1's IP Address: %s\n", player1.getInetAddress().getHostAddress());
                            System.out.print(message1);
                            System.out.print(message2);
                            log.appendText(message1);
                            log.appendText(message2);
                        });
                        player2 = serverSocket.accept();
                        Platform.runLater(() -> {
                            String message1 = String.format("%s: Player 2 joined the session.\n", new Date().toString());
                            String message2 = String.format("Player 2's IP Address: %s\n", player2.getInetAddress().getHostAddress());
                            System.out.print(message1);
                            System.out.print(message2);
                            log.appendText(message1);
                            log.appendText(message2);
                        });
                        Thread session = new Thread(new HandleASession(log,player1,player2));
                        session.start();
                    }
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        };
        server.start();
    }
}

class HandleASession implements Runnable{
    private TextArea log;
    private Socket player1;
    private Socket player2;
    private DataInputStream fromPlayer1;
    private DataOutputStream toPlayer1;
    private DataInputStream fromPlayer2;
    private DataOutputStream toPlayer2;
    public HandleASession(TextArea log, Socket player1, Socket player2){
        this.log = log;
        this.player1 = player1;
        this.player2 = player2;
    }
    @Override
    public void run() {
        try {
            // Create data input and output streams
            fromPlayer1 = new DataInputStream(player1.getInputStream());
            toPlayer1 = new DataOutputStream(player1.getOutputStream());
            fromPlayer2 = new DataInputStream(player2.getInputStream());
            toPlayer2 = new DataOutputStream(player2.getOutputStream());
            // Notify Players Ready
            System.out.println("Waiting for Player 1...");
            toPlayer1.writeInt(1);
            System.out.println("Player 1 is Ready!");
            System.out.println("Waiting for Player 2...");
            toPlayer2.writeInt(1);
            System.out.println("Player 2 is Ready!");
            // Identify Players
            toPlayer1.writeInt(0);
            System.out.println("Identified Player 1 as X.");
            toPlayer2.writeInt(1);
            System.out.println("Identified Player 2 as O.");
            while (true) {
                int player1Move = fromPlayer1.readInt();
                System.out.println("Read Player 1's Move.");
                toPlayer2.writeInt(player1Move);
                System.out.println("Sent Move from Player 1 to Player 2.");
                int player2Move = fromPlayer2.readInt();
                System.out.println("Read Player 2's Move.");
                toPlayer1.writeInt(player2Move);
                System.out.println("Sent Move from Player 2 to Player 1.");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
