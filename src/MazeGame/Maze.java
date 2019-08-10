package MazeGame;

import java.util.ArrayList;
import java.util.Stack;

public class Maze {

    private int length, height;
    private Node[][] nodes;

    private final int[] moveX = {0, 1, 0, -1}; // top, right, bottom, left
    private final int[] moveY = {-1, 0, 1, 0};

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
