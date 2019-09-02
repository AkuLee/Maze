package MazeGame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.*;

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

        // Check if pathable and in-bound
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

        // If there's a wall, draw line.
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
                nodes[i][j].fScore = Double.MAX_VALUE;
                nodes[i][j].gScore = Double.MAX_VALUE;
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

            boolean first = true; // Setup
            boolean done = false; // Solved

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

                    // Update state of current node and stack
                    current = (Node) next[0];
                    stack = (Stack<Node>) next[1];

                    // If solved.
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

        showNode(current, view.offset, view.currentlyPathing);
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

                    showNode(current, view.offset, view.currentlyPathing);
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

    /**
     * Method that computes an approximation of the distance between a and b. The heuristic in the a* algorithm.
     * @param a node a
     * @param b node b
     * @return an approximation of the distance between a and b
     */
    public double heuristicAStar(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Method that solves a maze with a* algorithm, adopted for animation with AnimationTimer.
     */
    public void solveAStar() {
        resetSolve();

        AnimationTimer timer = new AnimationTimer() {
            double timeAcc = 0;
            long lastTime = 0;

            boolean first = true;
            boolean done = false;

            ArrayList<Node> openSet = new ArrayList<>(),
                            closedSet = new ArrayList<>();

            Node current;

            Node[][] nodes = maze.getNodes();
            Node start = nodes[0][0];
            Node end = nodes[nodes.length - 1][nodes[0].length - 1];

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

                    openSet.add(start);
                    start.fScore = 0;
                    start.gScore = heuristicAStar(start, end);
                }

                timeAcc += ((now - lastTime)/1000);
                lastTime = now;

                if (timeAcc >= trigger) { // Delay and limit frame animation gap

                    timeAcc -= trigger;

                    if (openSet.isEmpty()) { // Impossible
                        done = true;
                        return;
                    }

                    current = findBestFScore(openSet); // Find node with best f score

                    if (current == end) { // Solved
                        done = true;
                        return;
                    }

                    oneStepAStar(current, end, openSet, closedSet); // Update state of solution
                    oneStepAStarShow(openSet, closedSet); // Update display of solution
                }

            }
        };
        timer.start();
    }

    /**
     * Method for a* algorithm. Finds the node with the best f score in the open set.
     * @param openSet ArrayList<Node>
     * @return Node
     */
    public Node findBestFScore(ArrayList<Node> openSet) {
        Node best = null;
        for (Node node : openSet) {
            if (best == null) {
                best = node;
            }

            else if (node.fScore < best.fScore) {
                best = node;
            }
        }

        return best;
    }

    /**
     * Method updates solution of maze with a* algorithm
     * @param openSet ArrayList<Node> currently visiting nodes
     * @param closedSet ArrayList<Node> previously visited nodes
     */
    public void oneStepAStarShow(ArrayList<Node> openSet, ArrayList<Node> closedSet) {
        for (Node node : openSet) {
            showNode(node, view.offset, view.currentlyChecking);
        }

        for (Node node : closedSet) {
            showNode(node, view.offset, view.currentlyPathing);
        }
    }

    /**
     *
     * @param current the currently evaluated node
     * @param end the target exit
     * @param openSet ArrayList<Node> currently visited nodes
     * @param closedSet ArrayList<Node> previously visited nodes
     */
    public void oneStepAStar(Node current, Node end, ArrayList<Node> openSet, ArrayList<Node> closedSet) {
        // Current is now previously visited.
        openSet.remove(current);
        closedSet.add(current);

        // Find neighbours of current.
        ArrayList<Node> neighbours = maze.getNeighbours(current);
        if (neighbours.isEmpty()) {
            showNode(current, view.offset, view.wrongPathing);
        }

        // For every neighbour, update gscore and fscore and parent node if needed. If not previously visited node,
        // good to check and possibly update data.
        for (Node neighbour : neighbours) {
            if (closedSet.contains(neighbour)) {
                continue;
            }

            double tempGScore = current.gScore + 1;

            if (!openSet.contains(neighbour)) {
                openSet.add(neighbour);
            }

            if (tempGScore < neighbour.gScore) {
                neighbour.parent = current;
                neighbour.gScore = tempGScore;
                neighbour.fScore = neighbour.gScore + heuristicAStar(neighbour, end);
            }
        }
    }

    /**
     * Method will find the node with the minimal distance from the starting node in the queue.
     * @param queue ArrayLis of nodes
     * @return a node
     */
    public Node findBestMinDistFromStart(ArrayList<Node> queue) {
        Node bestMin = null;

        for (int i = 0; i < queue.size(); i++) {
            if (bestMin == null) { // Initialize
                bestMin = queue.get(i);
            }

            if (queue.get(i).gScore < bestMin.gScore) { // Update if found a better candidate.
                bestMin =  queue.get(i);
            }
        }

        return bestMin;
    }

    /**
     * Method that solves a maze with djikstra algorithm, adopted for animation with AnimationTimer.
     */
    public void solveDijkstra() {
        resetSolve();

        AnimationTimer timer = new AnimationTimer() {
            double timeAcc = 0;
            long lastTime = 0;

            boolean first = true;
            boolean done = false;

            ArrayList<Node> queue = new ArrayList<>();

            Node[][] nodes = maze.getNodes();
            Node end = nodes[nodes.length - 1][nodes[0].length - 1];
            Node current = nodes[0][0];

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

                    current.gScore = 0;
                    queue.add(current);
                }

                timeAcc += ((now - lastTime)/1000);
                lastTime = now;

                if (timeAcc >= trigger) { // Delay and limit frame animation gap

                    timeAcc -= trigger;

                    if (queue.isEmpty()) { // Impossible solution
                        //System.out.println("Queue is empty");
                        done = true;
                        return;
                    }

                    current = findBestMinDistFromStart(queue);
                    current.visited = true;
                    showNode(current, view.offset, view.currentlyPathing);

                    if (current == end) { // Solved
                        //System.out.println("Solution found");
                        done = true;
                        return;
                    }

                    oneStepDijkstra(current, queue); // Update state of solution
                }

            }
        };
        timer.start();
    }

    /**
     * Method that updates the solution with dijkstra one step at a time to faciliate animation.
     * @param current node
     * @param queue arraylist of nodes
     */
    public void oneStepDijkstra(Node current, ArrayList<Node> queue) {

        queue.remove(current);

        // Neighbours
        ArrayList<Node> neighbours = maze.getNeighbours(current);

        // For each unvisited neighbour, find the distance from current to neighbour. If new distance is better than
        // original distance of neighbour, update distance.
        for (Node neighbour : neighbours) {
            if (neighbour.visited) {
                continue;
            }

            showNode(neighbour, view.offset, view.currentlyChecking);

            double tempDist = current.gScore + 1; // New distance
            if (tempDist < neighbour.gScore) { // If better distance than original, update

                neighbour.gScore = tempDist;
                neighbour.parent = current;

                if (!queue.contains(neighbour)) { // If queue doesn't already contain the neighbour, add.
                    queue.add(neighbour);
                    //System.out.println("Add to queue");
                }

            }
        }
    }
}
