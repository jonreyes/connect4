package ui;

import core.Connect4;
import core.Connect4Client;
import core.GameThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Class
 * Object template for creating Game GUI
 * @author Jon Reyes
 */
public class Connect4GUI extends Application {
    private final double HEIGHT = 600;
    private final double WIDTH = 800;
    private HBox content;
    private GameThread t;
    private Connect4 game;
    private StringProperty titleProp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Group root = createRoot(primaryStage);
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setTitle("Connect 4");
            titleProp = primaryStage.titleProperty();
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e->{
                Connect4Client client = game.getClient();
                if(client!=null) client.close();
                Platform.exit();
                System.exit(0);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Group createRoot(Stage stage){
        Group root = new Group();
        content = uiContent(stage);
        root.getChildren().add(content);
        return root;
    }

    public HBox uiContent(Stage stage){
        HBox content = new HBox();
        content.prefWidthProperty().bind(stage.widthProperty());
        content.prefHeightProperty().bind(stage.heightProperty().multiply(0.95));
        BoardUI grid = boardUI(content);
        VBox menu = menuUI(content);
        content.getChildren().add(grid);
        content.getChildren().add(menu);
        content.setOnKeyPressed(e->{
            switch (e.getCode()){
                case M: menu.setTranslateX((menu.getTranslateX()==0)?menu.getWidth():0);
                    break;
                default:
                    break;
            }
        });
        content.setStyle("-fx-font-weight:bold;-fx-font-size:20px;");
        return content;
    }

    public BoardUI boardUI(HBox content){
        t = new GameThread();
        t.start();
        game = t.getGame();
        BoardUI grid = new BoardUI(game);
        grid.prefWidthProperty().bind(content.widthProperty().multiply(0.75));
        grid.prefHeightProperty().bind(content.heightProperty());
        logoUI(grid);
        game.idProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String symbol = game.idProperty().get();
                        String title = String.format("Connect 4 - Player %s", symbol);
                        titleProp.set(title);
                    }
                });
            }
        });
        return grid;
    }

    public void logoUI(BoardUI grid){
        if(game.opponentProperty().isNull().get()){
            try {
                Image logo = new Image(new FileInputStream("src/ui/Connect4Logo.png"));
                ImageView logoView = new ImageView(logo);
                logoView.visibleProperty().bind(game.opponentProperty().isNull());
                DoubleBinding w = grid.prefWidthProperty().divide(2);
                DoubleBinding h = grid.prefHeightProperty().divide(2);
                logoView.setPreserveRatio(true);
                logoView.fitWidthProperty().bind(grid.prefWidthProperty().multiply(0.64));
                logoView.xProperty().bind(w.subtract(logoView.fitWidthProperty().divide(2)));
                logoView.yProperty().bind(h.divide(2));
                grid.getChildren().add(logoView);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public VBox menuUI(HBox content){
        VBox menu = new VBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(10);
        menu.prefWidthProperty().bind(content.widthProperty().multiply(0.25));
        menu.prefHeightProperty().bind(content.heightProperty());
        Text title = new Text("CONNECT 4");
        title.setTextAlignment(TextAlignment.CENTER);
        HBox scores = scoreUI();
        Button resetBtn = new Button("Reset");
        resetBtn.visibleProperty().bind(
                game.scoreProperty()[0].add(game.scoreProperty()[1]).greaterThan(0)
                        .or(game.opponentProperty().isNotNull()));
        resetBtn.setOnMouseClicked(e->{
            if(game.getClient()!=null) {
                game.onlineProperty().set(false);
            }
            titleProp.set("Connect 4");
            game.scoreProperty()[0].set(0);
            game.scoreProperty()[1].set(0);
            content.getChildren().clear();
            content.getChildren().add(boardUI(content));
            content.getChildren().add(menuUI(content));
        });
        VBox goUI = gameOverUI();
        VBox versusUI = versusUI();
        VBox statusUI = statusUI();
        menu.getChildren().add(title);
        menu.getChildren().add(scores);
        menu.getChildren().add(resetBtn);
        menu.getChildren().add(goUI);
        menu.getChildren().add(versusUI);
        menu.getChildren().add(statusUI);
        menu.setStyle("-fx-border-color: #3c5bab;-fx-border-width:15;");
        return menu;
    }

    public HBox scoreUI(){
        HBox scores = new HBox();
        scores.visibleProperty().bind(game.scoreProperty()[0].add(game.scoreProperty()[1]).greaterThanOrEqualTo(0));
        scores.setSpacing(10);
        scores.setAlignment(Pos.CENTER);
        VBox wins = createWins();
        VBox lost = createLost();
        scores.getChildren().add(wins);
        scores.getChildren().add(lost);
        return scores;
    }

    public VBox createWins(){
        VBox wins = new VBox();
        wins.setAlignment(Pos.CENTER);
        Text winScoreText = new Text("WIN");
        Text winScore = new Text();
        winScore.textProperty().bind(game.scoreProperty()[0].asString());
        wins.getChildren().add(winScoreText);
        wins.getChildren().add(winScore);
        return wins;
    }

    public VBox createLost(){
        VBox lost = new VBox();
        lost.setAlignment(Pos.CENTER);
        Text lostScoreText = new Text("LOSE");
        Text lostScore = new Text();
        lostScore.textProperty().bind(game.scoreProperty()[1].asString());
        lost.getChildren().add(lostScoreText);
        lost.getChildren().add(lostScore);
        return lost;
    }

    public VBox versusUI(){
        VBox versusUI = new VBox();
        versusUI.visibleProperty().bind(game.opponentProperty().isNull());
        versusUI.setSpacing(10);
        versusUI.setAlignment(Pos.CENTER);
        Text vText = new Text("vs.");
        Button playerBtn = new Button("Player (Local)");
        playerBtn.setOnMouseClicked(e->{
            game.opponentProperty().set("P");
            versusUI.getChildren().clear();
        });
        Button onlineBtn = new Button("Player (Online)");
        onlineBtn.setOnMouseClicked(e->{
            game.onlineProperty().set(true);
            game.opponentProperty().set("P");
            versusUI.getChildren().clear();
        });
        Button cpuBtn = new Button("CPU");
        cpuBtn.setOnMouseClicked(e->{
            game.opponentProperty().set("C");
            versusUI.getChildren().clear();
        });
        Button testBtn = new Button("Auto");
        testBtn.setOnMouseClicked(e->{
            game.opponentProperty().set("T");
            versusUI.getChildren().clear();
        });
        versusUI.getChildren().add(vText);
        versusUI.getChildren().add(playerBtn);
        versusUI.getChildren().add(onlineBtn);
        versusUI.getChildren().add(cpuBtn);
        versusUI.getChildren().add(testBtn);
        return versusUI;
    }

    public VBox gameOverUI(){
        VBox goUI = new VBox();
        goUI.setSpacing(10);
        goUI.setAlignment(Pos.CENTER);
        goUI.visibleProperty().bind(game.statusProperty().greaterThan(0));
        Text winText = new Text();
        winText.textProperty().bind(game.resultProperty());
        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setOnMouseClicked(e->{
            titleProp.set("Connect 4");
            Connect4 oldGame = game;
            content.getChildren().clear();
            content.getChildren().add(boardUI(content));
            content.getChildren().add(menuUI(content));
            game.scoreProperty()[0].set(oldGame.scoreProperty()[0].get());
            game.scoreProperty()[1].set(oldGame.scoreProperty()[1].get());
        });
        goUI.getChildren().add(winText);
        goUI.getChildren().add(playAgainBtn);
        return goUI;
    }

    public VBox statusUI(){
        VBox statusUI = new VBox();
        statusUI.setAlignment(Pos.CENTER);
        statusUI.visibleProperty().bind(game.statusProperty().isEqualTo(0));
        Text waitText = new Text();
        waitText.visibleProperty().bind(game.turnProperty().lessThan(1).and(game.onlineProperty()));
        waitText.setText("Waiting for Players");
        Text dotText = new Text();
        dotText.visibleProperty().bind(game.turnProperty().lessThan(1).and(game.onlineProperty()));
        statusUI.getChildren().add(waitText);
        statusUI.getChildren().add(dotText);
        Thread dotTime = new Thread(){
            public void run(){
                try {
                    while(game.turnProperty().lessThan(1).get()) {
                        dotText.setText(".");
                        Thread.sleep(200);
                        dotText.setText("..");
                        Thread.sleep(200);
                        dotText.setText("...");
                        Thread.sleep(200);
                        dotText.setText("");
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        dotTime.start();
        Text turnText = new Text();
        turnText.visibleProperty().bind(game.turnProperty().greaterThan(0));
        turnText.setTextAlignment(TextAlignment.CENTER);
        turnText.textProperty().bind(Bindings.format("Turn\n%s",game.turnProperty().asString()));
        Text activeText = new Text();
        activeText.setTextAlignment(TextAlignment.CENTER);
        activeText.visibleProperty().bind(game.activeProperty().isNotNull());
        activeText.textProperty().bind(Bindings.format("%s's\nTurn.",game.activeProperty().asString()));
        statusUI.getChildren().add(turnText);
        statusUI.getChildren().add(activeText);
        return statusUI;
    }
}
