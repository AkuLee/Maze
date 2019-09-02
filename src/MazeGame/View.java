package MazeGame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class View extends Application {

    int offset = 5;

    int size = 10;

    int length = 60, height = 60;

    static Color ground = Color.rgb(125, 125, 125),
                 wall = Color.rgb(65, 50, 125),
                 player = currentlyChecking = Color.rgb(225, 225, 200);

    static Color solved = Color.rgb(225, 150, 150),
                 wrongPathing = Color.rgb(100, 150, 100),
                 currentlyChecking = Color.rgb(150, 150, 255),
                 currentlyPathing = Color.rgb(75, 75, 200);

    GraphicsContext context;
    Controller controller;

    @Override
    public void start(Stage stage) throws Exception {

        // Canvas
        Canvas canvas = new Canvas(size * length + offset * 2, size* height + offset * 2);
        context = canvas.getGraphicsContext2D();

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed( event -> {
            // System.out.println("something is happening"); todo debug
            controller.updatePlayer(event.getCode());
        });

        this.controller = new Controller(this, length, height);
        controller.setup();

        // Side menu items
        Text header = new Text("Configuration");
        header.setFont(Font.font(null, FontWeight.BOLD, 20));

        // --- Text ---
        Text    first = new Text("Length: "),
                snd = new Text("Height: ");

        // --- Spinner settings ---
        Spinner lengthText = new Spinner(5, 115, length),
                heightText = new Spinner(5, 70, height);

        lengthText.setPrefSize(75, 30);
        heightText.setPrefSize(75, 30);

        // --- Align settings input vertically ---
        HBox firstLine = new HBox(first, lengthText); firstLine.setAlignment(Pos.CENTER);
        HBox sndLine = new HBox(snd, heightText); sndLine.setAlignment(Pos.CENTER);

        Button dfsSolve = new Button("Solve with DFS");
        dfsSolve.setOnAction( event -> {
            controller.solveDFS();
        });

        Button bfsSolve = new Button("Solve with BFS");
        bfsSolve.setOnAction( event -> {
            controller.solveBFS();
        });

        Button aStarSolve = new Button("Solve with A*");
        aStarSolve.setOnAction( event -> {
            controller.solveAStar();
        });

        Button dijkstraSolve = new Button("Solve with Dijkstra");
        dijkstraSolve.setOnAction( event -> {
            controller.solveDijkstra();
        });

        // todo sample, random mouse, wall follower, pledge, trémaux, dead-end filling, recursive, maze-routing, shortestpath

        Button clear = new Button("Clear path");
        clear.setOnAction( event -> {
            controller.setup();
        });

        Button anotherMaze = new Button("Generate another maze");
        anotherMaze.setOnAction( event -> {
            length = (int) lengthText.getValue();
            height = (int) heightText.getValue();

            // Canvas
            Canvas newCanvas = new Canvas(size * length + offset * 2, size* height + offset * 2);
            context = newCanvas.getGraphicsContext2D();

            newCanvas.setFocusTraversable(true);
            newCanvas.setOnKeyPressed( someEvent -> {
                // System.out.println("something is happening"); todo debug
                controller.updatePlayer(someEvent.getCode());
            });

            this.controller = new Controller(this, length, height);
            controller.setup();

            // Side menu
            VBox side = new VBox(header, firstLine, sndLine, anotherMaze,
                    dfsSolve, bfsSolve, aStarSolve, dijkstraSolve, clear);
            side.setAlignment(Pos.TOP_CENTER);
            side.setPrefHeight(canvas.getHeight());
            side.setPrefWidth(300);

            stage.setScene(new Scene(new HBox(newCanvas, side)));
        });

        // Side menu with buttons
        VBox side = new VBox(header, firstLine, sndLine, anotherMaze,
                dfsSolve, bfsSolve, aStarSolve, dijkstraSolve, clear);
        side.setAlignment(Pos.TOP_CENTER);
        side.setPrefHeight(canvas.getHeight());
        side.setPrefWidth(300);

        // Screen
        HBox root = new HBox(canvas, side);
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setAlignment(Pos.CENTER);

        stage.setTitle("Maze");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
