package ui;

import core.Board;

import core.Connect4;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BoardUI extends Pane {
    private Circle[][] slots;
    private Connect4 game;

    public BoardUI(Connect4 game){
        this.game = game;
        createSlots();
        handleHighlight();
        handleClick();
        this.setStyle("-fx-background-color: #3c5bab;");
    }

    public void createSlots(){
        Board b = this.game.getBoard();
        slots = new Circle[b.getRows()][b.getCols()];
        DoubleBinding w = this.widthProperty().divide(b.getCols());
        DoubleBinding h = this.heightProperty().divide(b.getRows());
        DoubleBinding r = (w.get()<h.get())? w.divide(2): h.divide(2);
        for(int i = 0; i < b.getCols(); i++){
            for(int j = 0; j < b.getRows(); j++){
                Circle slot = new Circle();
                ObservableValue x = w.multiply(i);
                ObservableValue y = h.multiply(j);
                slot.centerXProperty().bind(x);
                slot.centerYProperty().bind(y);
                slot.radiusProperty().bind(r.subtract(10));
                slot.translateXProperty().bind(w.divide(2));
                slot.translateYProperty().bind(h.divide(2));
                slot.fillProperty().bind(b.colorProperty(j,i));
                slot.visibleProperty().bind(game.opponentProperty().isNotNull());
                slots[j][i] = slot;
                this.getChildren().add(slot);
            }
        }
    }

    public void handleHighlight(){
        Board b = this.game.getBoard();
        DoubleBinding w = this.widthProperty().divide(b.getCols());
        DoubleBinding h = this.heightProperty().divide(b.getRows());
        DoubleBinding r = (w.get()<h.get())? w.divide(2): h.divide(2);
        Rectangle highlight = new Rectangle();
        this.getChildren().add(highlight);
        Text column = new Text();
        this.getChildren().add(column);
        this.setOnMouseMoved(e->{
            if(this.game.opponentProperty().isNotNull().get()) {
                int mouseToCol = (int) Math.floor(e.getX() / w.get());
                column.setText(String.valueOf(mouseToCol + 1));
                column.xProperty().bind(w.multiply(mouseToCol).add(w.divide(2)));
                column.setY(h.divide(2).get());
                highlight.setFill(Color.TRANSPARENT);
                highlight.setStroke(Color.BLACK);
                highlight.setStrokeWidth(5);
                highlight.xProperty().bind(w.multiply(mouseToCol));
                highlight.setY(0);
                highlight.widthProperty().bind(w);
                highlight.heightProperty().bind(this.heightProperty());
                //System.out.println(mouseToCol);
            }
        });
    }

    public void handleClick(){
        Board b = this.game.getBoard();
        DoubleBinding w = this.widthProperty().divide(b.getCols());
        this.setOnMouseClicked(e->{
            if(game.getClient()!=null&&game.getActive()!=null){
                if(game.getActive().getSymbol().equals(game.getClient().getSymbol())) {
                    int mouseToCol = (int) Math.floor(e.getX() / w.get());
                    game.moveProperty().set(-1);
                    game.moveProperty().set(mouseToCol);
                }
            } else {
                int mouseToCol = (int) Math.floor(e.getX() / w.get());
                game.moveProperty().set(-1);
                game.moveProperty().set(mouseToCol);
            }
        });
    }

    public void showCoordinates(){
        Board b = this.game.getBoard();
        for(int i=0; i < b.getCols(); i++){
            for(int j=0; j < b.getRows(); j++){
                ObservableValue x = this.widthProperty().divide(b.getCols()).multiply(i);
                ObservableValue y = this.heightProperty().divide(b.getRows()).multiply(j);
                Text t = new Text();
                t.setText(String.format("%d%d",j-1,i));
                t.xProperty().bind(x);
                t.yProperty().bind(y);
                this.getChildren().add(t);
            }
        }
    }
    public void showGridLines(){
        Board b = this.game.getBoard();
        for(int i = 0; i < b.getCols(); i++){
            ObservableValue windowWidth = this.widthProperty().divide(b.getCols()).multiply(i);
            Line line =  new Line();
            line.startXProperty().bind(windowWidth);
            line.setStartY(0);
            line.endXProperty().bind(windowWidth);
            line.endYProperty().bind(this.heightProperty());
            this.getChildren().add(line);
        }

        for(int i = 0; i < b.getRows(); i++){
            ObservableValue windowHeight = this.heightProperty().divide(b.getRows()).multiply(i);
            Line line = new Line();
            line.setStartX(0);
            line.startYProperty().bind(windowHeight);
            line.endXProperty().bind(this.widthProperty());
            line.endYProperty().bind(windowHeight);
            this.getChildren().add(line);
        }
    }
}
