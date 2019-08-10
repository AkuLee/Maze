package MazeGame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Stack;

public class Controller {

    private Player player;

    private Maze maze;
    private View view;

    public Controller(View view, int length, int height) {

        this.player = new Player(0, 0);

        this.view = view;
        maze = new Maze(length, height); // maze = new Maze(size, height);

        setup();
    }

    public void setup() {
        Node[][] nodes = maze.getNodes();

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                showNode(nodes[i][j], view.offset, view.ground);
            }
        }

        showPlayer();
    }

    public void updatePlayer(KeyCode keyCode) {

        Node node = maze.getNodes()[player.getY()][player.getX()];
        clearPlayer();

        switch(keyCode) {
            case UP:
                if (!node.walls[0] && (player.getY() - 1) >= 0) {
                    //System.out.println("UP"); todo debug
                    player.moveUp();
                }
                break;

            case DOWN:
                if (!node.walls[2] && (player.getY() + 1) < maze.getNodes().length) {
                    //System.out.println("DOWN"); todo debug
                    player.moveDown();

                }
                break;

            case LEFT:
                if (!node.walls[3] && (player.getX() - 1) >= 0) {
                    //System.out.println("LEFT"); todo debug
                    player.moveLeft();
                }
                break;

            case RIGHT:
                if (!node.walls[1] && (player.getX() + 1) < maze.getNodes()[0].length) {
                    //System.out.println("RIGHT"); todo debug
                    player.moveRight();
                }
                break;
        }

        showPlayer();
    }

    public void showPlayer() {
        view.context.setFill(view.player);
        view.context.fillRect(
                player.getX() * view.size + view.size * 0.25 + view.offset,
                player.getY() * view.size + view.size * 0.25 + view.offset,
                view.size * 0.5, view.size * 0.5);
    }

    public void clearPlayer() {
        showNode(maze.getNodes()[player.getY()][player.getX()], view.offset, view.ground);
    }

    public void showNode(Node node, int offset, Color color) {

        GraphicsContext context = view.context;
        int nodeX = node.x * view.size, nodeY = node.y * view.size;

        context.setFill(color);
        context.fillRect(nodeX + offset, nodeY + offset, view.size, view.size);

        context.setStroke(view.wall);
        context.setLineWidth(2);

        if (node.walls[0]) { // top
            context.strokeLine(nodeX + offset, nodeY + offset,
                    nodeX + view.size + offset, nodeY + offset);
        }

        if (node.walls[1]) { // right
            context.strokeLine(nodeX + view.size + offset, nodeY + offset,
                    nodeX + view.size + offset, nodeY + view.size + offset);
        }

        if (node.walls[2]) { // bottom
            context.strokeLine(nodeX + view.size + offset, nodeY + view.size + offset,
                    nodeX + offset, nodeY + view.size + offset);
        }

        if (node.walls[3]) { // left
            context.strokeLine(nodeX + offset, nodeY + view.size + offset,
                    nodeX + offset, nodeY + offset);
        }

    }

    public void solve() {

        setUnvisited();

        AnimationTimer timer = new AnimationTimer() {
            double timeAcc = 0;
            long lastTime = 0;

            boolean first = true;
            boolean done = false;

            Stack<Node> stack;
            Node current;

            double trigger = 10000; // Delay in animation

            @Override
            public void handle(long now) {

                if (done) {

                    showNode(current, view.offset, view.solved);
                    if (!stack.empty()) {
                        current = stack.pop();
                    }

                    return;
                }

                if (first) { // Setup
                    lastTime = now;
                    first = false;

                    stack = new Stack<>();
                    current = maze.getNodes()[0][0];
                    current.visited = true;
                }

                timeAcc += ((now - lastTime)/1000);
                lastTime = now;

                if (timeAcc >= trigger) {
                    Object[] next = oneStep(current, stack);
                    current = (Node) next[0];
                    stack = (Stack<Node>) next[1];

                    if (current.x == maze.getNodes()[0].length - 1 && current.y == maze.getNodes().length - 1) {
                        done = true;
                    }

                    timeAcc -= trigger;
                }

            }
        };
        timer.start();
    }

    public void setUnvisited() {
        Node[][] nodes = maze.getNodes();

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j].visited = false;
            }
        }
    }

    public Object[] oneStep(Node current, Stack<Node> stack) {

        Node neighbour = maze.getANeighbourPath(current);

        if (neighbour == null) {

            if (!stack.empty()) {
                showNode(current, view.offset, view.wrongPathing);

                current = stack.pop();
                showNode(current, view.offset, view.currentlyChecking);

                return new Object[] {current, stack};
            }

            return new Object[] {current, stack};
        }

        stack.push(current);

        showNode(current, view.offset, view.currentlyPathing);
        showNode(neighbour, view.offset, view.currentlyChecking);

        neighbour.visited = true;
        current = neighbour;

        return new Object[] {current, stack};
    }
}
