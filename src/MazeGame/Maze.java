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

    public void gen_backtrack_DFS() {

        Stack<Node> stack = new Stack<>();

        Node current = nodes[0][0];
        current.visited = true;
        current.walls[0] = false;

        while (true) {

            Node neighbour = getANeighbour(current);

            if (neighbour == null) {

                if (!stack.empty()) {
                    current = stack.pop();
                    continue;
                }

                return;
            }

            stack.push(current);
            Node.removeCommonWall(current, neighbour);

            neighbour.visited = true;
            current = neighbour;

            if (current.x == (nodes[0].length - 1) && current.y == (nodes.length - 1)) {
                current.walls[2] = false;
            }
        }
    }

    public void bfs_solve() {
        LinkedList<Node> queue = new LinkedList<>();
        Node current = nodes[0][0];
        current.visited = true;
        queue.addLast(current);

        while (!queue.isEmpty()) {
            current = queue.removeFirst();

            if (current.x == nodes[0].length - 1 && current.y == nodes.length - 1) {
                return;
            }

            ArrayList<Node> neighbours = getNeighbours(current);
            for (Node neighbour : neighbours) {
                neighbour.visited = true;
                neighbour.parent = current;
                queue.addLast(neighbour);
            }
        }

    }

    public Node getANeighbour (Node current) {

        ArrayList<Node> unvisited = new ArrayList<>();

        for (int i = 0; i < moveX.length; i++) { // Get all unvisited nodes.
            int x = current.x + moveX[i], y = current.y + moveY[i];

            if (inBound(x, y) && !nodes[y][x].visited) unvisited.add(nodes[y][x]);
        }

        if (unvisited.size() == 0) {
            return null;
        } else {
            int randIdx = (int) Math.floor(Math.random() * unvisited.size());
            return unvisited.get(randIdx);
        }
    }

    public ArrayList<Node> getNeighbours(Node current) {

        ArrayList<Node> neighbours = new ArrayList<>();

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

    public Node getANeighbourPath (Node current) {

        ArrayList<Node> unvisited = new ArrayList<>();

        for (int i = 0; i < moveX.length; i++) { // Get all unvisited nodes.
            int x = current.x + moveX[i], y = current.y + moveY[i];

            if (inBound(x, y) && !nodes[y][x].visited && !current.walls[i]) {
                unvisited.add(nodes[y][x]);
            }
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
}
