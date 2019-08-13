package MazeGame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
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

    /**
     * Sets up the maze, the player square
     */
    public void setup() {
        Node[][] nodes = maze.getNodes();

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                showNode(nodes[i][j], view.offset, view.ground);
            }
        }

        showPlayer();
    }

    /**
     * Method that updates a player move if up, down, left or right arrows are pressed.
     * @param keyCode the keycode
     */
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

    /**
     * Method to show the player as a small square shape
     */
    public void showPlayer() {
        view.context.setFill(view.player);
        view.context.fillRect(
                player.getX() * view.size + view.size * 0.25 + view.offset,
                player.getY() * view.size + view.size * 0.25 + view.offset,
                view.size * 0.5, view.size * 0.5);
    }

    /**
     * Remove player from the current position by filling the cell it is on with the background color
     */
    public void clearPlayer() {
        showNode(maze.getNodes()[player.getY()][player.getX()], view.offset, view.ground);
    }

    /**
     * Method shows a node on the GUI according to the color
     * @param node a Node
     * @param offset the offset for the grid
     * @param color the color of the node
     */
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

    /**
     * Method to reset all nodes to unvisited so we can solve the maze.
     */
    public void resetSolve() {
        Node[][] nodes = maze.getNodes();

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j].visited = false;
                nodes[i][j].parent = null;
            }
        }
    }

    /**
     * Method solves the maze with dfs backtracking algorithm and animates it with an animation timer. Each call showing
     * one whole step of the algorithm.
     */
    public void solveDFS() {

        resetSolve();

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

                if (timeAcc >= trigger) { // Delay and limit frame animation gap
                    Object[] next = oneStepDFS(current, stack);
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

    /**
     * Method for simulating a one-step of the dfs backtrack algorithm in adaptation to the use of the animation timer.
     * @param current the currently checked node
     * @param stack the stack for the algorithm
     * @return an array of size 2 containing the new current node and the new state of the stack.
     */
    public Object[] oneStepDFS(Node current, Stack<Node> stack) {

        Node neighbour = maze.getANeighbourPath(current); // Find an unvisited neighbour.

        if (neighbour == null) { // If no neighbours.

            if (!stack.empty()) { // Pop and backtrack on previous visited node.
                //showNode(current, view.offset, view.wrongPathing); for visualibility

                current = stack.pop();
                showNode(current, view.offset, view.currentlyChecking);

                return new Object[] {current, stack};
            }

            return new Object[] {current, stack};
        }

        stack.push(current); // Check next neighbour.

        // showNode(current, view.offset, view.currentlyPathing); for visualibility
        showNode(current, view.offset, view.currentlyChecking);
        showNode(neighbour, view.offset, view.currentlyChecking);

        neighbour.visited = true;
        current = neighbour;

        return new Object[] {current, stack};
    }

    /**
     * Method that solves the maze with bfs algorithm.
     * @see Maze#bfs_solve()
     */
    public void solveBFS() {

        resetSolve();

        AnimationTimer timer = new AnimationTimer() {
            double timeAcc = 0;
            long lastTime = 0;

            boolean first = true;
            boolean done = false;

            LinkedList<Node> queue;
            Node current;

            double trigger = 10000; // Delay in animation

            @Override
            public void handle(long now) {

                if (done) { // If maze is solved

                    showNode(current, view.offset, view.solved);
                    if (current.parent != null) {
                        current = current.parent;
                    }

                    return;
                }

                if (first) { // Setup
                    lastTime = now;
                    first = false;

                    queue = new LinkedList<>();
                    current = maze.getNodes()[0][0];
                    current.visited = true;
                    queue.addLast(current);
                }

                timeAcc += ((now - lastTime)/1000);
                lastTime = now;

                if (timeAcc >= trigger) { // Delay and limit frame animation gap

                    current = queue.removeFirst();

                    if (current.x == maze.getNodes()[0].length - 1 && current.y == maze.getNodes().length - 1) {
                        done = true;
                        return;
                    }

                    queue = oneStepBFS(current, queue);
                    timeAcc -= trigger;
                }

            }
        };
        timer.start();
    }

    /**
     * Method that simulates one step of the bfs algorithm at a time.
     * @param current currently evaluated node
     * @param queue the current state of the queue
     * @return the new state of the queue
     */
    public LinkedList<Node> oneStepBFS(Node current, LinkedList<Node> queue) {

        showNode(current, view.offset, view.currentlyChecking);

        ArrayList<Node> neighbours = maze.getNeighbours(current);
        for (Node neighbour : neighbours) {
            neighbour.visited = true;
            neighbour.parent = current;
            queue.addLast(neighbour);
        }

        return queue;
    }
}
