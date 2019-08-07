package BackTrack_DFS_Gen;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Stack;

public class MainView extends Application {

    Color unchecked = Color.rgb(150, 150, 150),
          currentlyChecking = Color.rgb(150, 150, 255),
          alreadyChecked = Color.rgb(100, 150, 100),
          previouslyChecked = Color.rgb(100, 100, 175);

    Stack<Node> stack;
    Node[][] nodes = new Node[60][125];
    Node current;

    Pane root;
    GraphicsContext context;
    int length = 10;

    @Override
    public void start(Stage stage) throws Exception {

        Canvas canvas = new Canvas(length * nodes[0].length, length * nodes.length);
        root = new Pane(canvas);

        setup(canvas);

        stage.setScene(new Scene(root));
        stage.show();

        // animation timer acts as a while loop
        AnimationTimer timer = new AnimationTimer() {

            double timeAcc = 0;
            long lastTime = 0;
            boolean first = true;

            double trigger = 100000; // Delay in animation

            @Override
            public void handle(long now) {

                if (first) { // Setup
                    lastTime = now;
                    first = false;

                    // give option to speed up or slow down animation
                    root.setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            System.out.println("Left button");
                            trigger /= 10;
                        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            System.out.println("Right button");
                            trigger *= 10;
                        }
                    });

                    return;
                }

                timeAcc += ((now - lastTime)/1000);
                lastTime = now;

                if (timeAcc >= trigger) {
                    oneStepGenerator();
                    timeAcc -= (trigger);
                }

            }
        };

        timer.start();
    }

    public void setup(Canvas canvas) {

        context = canvas.getGraphicsContext2D();
        stack = new Stack<>();

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = new Node(length, j, i);
                nodes[i][j].show(context, unchecked);
            }
        }

        current = nodes[0][0];
        current.visited = true;
        current.show(context, currentlyChecking);
    }

    public void oneStepGenerator() {

        Node neighbour = getANeighbour(current);

        if (neighbour == null) {

            if (!stack.empty()) {
                current.show(context, alreadyChecked);

                current = stack.pop();
                current.show(context, currentlyChecking);

                return;
            }

            return;
        }

        stack.push(current);
        Node.removeCommonWall(current, neighbour);

        current.show(context, previouslyChecked);
        neighbour.show(context, currentlyChecking);

        neighbour.visited = true;
        current = neighbour;
    }

    public Node getANeighbour(Node current) {

        int[] moveX = {0, 1, 0, -1};
        int[] moveY = {-1, 0, 1, 0};

        ArrayList<Node> unvisited = new ArrayList<>();

        for (int i = 0; i < moveX.length; i++) { // Get all unvisited nodes.
            int x = current.x + moveX[i], y = current.y + moveY[i];

            if (inBound(x, y) &&!nodes[y][x].visited) unvisited.add(nodes[y][x]);
        }

        if (unvisited.size() == 0) {
            return null;
        } else {
            int randIdx = (int) Math.floor(Math.random() * unvisited.size());
            return unvisited.get(randIdx);
        }
    }

    public boolean inBound(int x, int y) {
        return 0 <= x && x < nodes[0].length && 0 <= y && y < nodes.length;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
