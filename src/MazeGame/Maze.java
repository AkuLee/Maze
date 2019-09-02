package MazeGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Maze {

    private int length, height;
    private Node[][] nodes;

    public static final int[] moveX = {0, 1, 0, -1}; // top, right, bottom, left
    public static final int[] moveY = {-1, 0, 1, 0};

    public Maze(int length, int height) {
        nodes = new Node[height][length];

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = new Node(length, j, i);
            }
        }

        gen_backtrack_DFS();
    }

    public Node[][] getNodes() {
        return nodes;
    }

    /**
     * Method generates a maze with backtracking DFS. Algorithm on Wikipedia.
     */
    public void gen_backtrack_DFS() {

        Stack<Node> stack = new Stack<>();

        Node current = nodes[0][0];
        current.visited = true;
        current.walls[0] = false;

        while (true) {

            // Choose a random unvisited neighbour
            Node neighbour = getANeighbour(current);

            // If there is no more unvisited neighbours, backtrack and check the most recently visited node.
            if (neighbour == null) {

                if (!stack.empty()) {
                    current = stack.pop();
                    continue;
                }

                return; // If backtrack stack is empty, end the algorithm. We finished generating a maze.
            }

            stack.push(current); // Push node for backtracking.
            Node.removeCommonWall(current, neighbour); // Remove the wall

            neighbour.visited = true; // The chosen neighbour is now visited
            current = neighbour; // Evaluate this neighbour now

            // If end point of maze (bottom right of right) is reached, default the removal of wall.
            if (current.x == (nodes[0].length - 1) && current.y == (nodes.length - 1)) {
                current.walls[2] = false;
            }
        }
    }

    /**
     * Method solves a maze with BFS. Algorithm on Wikipedia
     */
    public void bfs_solve() {

        LinkedList<Node> queue = new LinkedList<>();

        // Starting point.
        Node current = nodes[0][0];
        current.visited = true;

        queue.addLast(current);

        while (!queue.isEmpty()) { // While queue is not empty
            current = queue.removeFirst(); // Remove the next node to check

            // If next node is the target exit, stop algorithm. Solution is found.
            if (current.x == nodes[0].length - 1 && current.y == nodes.length - 1) {
                return;
            }

            // For each of the next node's unvisited neighbours, add to the queue to check.
            ArrayList<Node> neighbours = getNeighbours(current);
            for (Node neighbour : neighbours) {
                neighbour.visited = true; // Visited.
                neighbour.parent = current; // Keep track of which node we came from.
                queue.addLast(neighbour); // Add to queue
            }
        }

    }

    /**
     * Method will find a random unvisited neighbours of the input node.
     * @param current an instance of Node
     * @return an instance of Node neighbour of current
     */
    public Node getANeighbour(Node current) {

        ArrayList<Node> unvisited = new ArrayList<>();

        // Get all unvisited nodes.
        for (int i = 0; i < moveX.length; i++) {
            int x = current.x + moveX[i], y = current.y + moveY[i];

            if (inBound(x, y) && !nodes[y][x].visited) unvisited.add(nodes[y][x]);
        }

        if (unvisited.size() == 0) { // Return nothing
            return null;
        } else { // Return a random node from the list.
            int randIdx = (int) Math.floor(Math.random() * unvisited.size());
            return unvisited.get(randIdx);
        }
    }

    /**
     * Method that will find all unvisited neighbours and return them in a list.
     * @param current the node to evaluate
     * @return the node's neighbours in a ArrayList<Node>
     */
    public ArrayList<Node> getNeighbours(Node current) {

        ArrayList<Node> neighbours = new ArrayList<>();

        // Four possible neighbours (no diagonal neighbours or pathing)
        for (int i = 0; i < 4; i++) {
            int x = current.x + Maze.moveX[i];
            int y = current.y + Maze.moveY[i];

            // in-bound, and side of this current is open, and neighbour not visited.
            if (inBound(x, y) && !current.walls[i] && !nodes[y][x].visited) {
                neighbours.add(nodes[y][x]);
            }
        }

        return neighbours;
    }

    /**
     * Method finds a random unvisited neighbour that can be pathed to from current node.
     * @param current an instance of Node
     * @return a Node
     */
    public Node getANeighbourPath (Node current) {

        ArrayList<Node> unvisited = new ArrayList<>();

        for (int i = 0; i < moveX.length; i++) { // Get all unvisited nodes.
            int x = current.x + moveX[i], y = current.y + moveY[i];

            if (inBound(x, y) && !nodes[y][x].visited && !current.walls[i]) {
                unvisited.add(nodes[y][x]);
            }
        }

        if (unvisited.size() == 0) { // Return nothing
            return null;
        } else { // Return a random node.
            int randIdx = (int) Math.floor(Math.random() * unvisited.size());
            return unvisited.get(randIdx);
        }
    }

    /**
     * Method checks if (x, y) is in-bound and respects array.
     * @param x x position int
     * @param y y position int
     * @return true if in-bound. False otherwise
     */
    public boolean inBound(int x, int y) {
        return 0 <= x && x < nodes[0].length && 0 <= y && y < nodes.length;
    }
}
