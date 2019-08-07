import java.util.ArrayList;
import java.util.Stack;

public class MazeGenerator {

    private Node[][] nodes;

    int[] moveX = {0, 1, 0, -1};
    int[] moveY = {-1, 0, 1, 0};

    public MazeGenerator(int length, int height) {
        nodes = new Node[height][length];

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = new Node(length, j, i);
            }
        }

        backTrack_DFS();
    }

    public void backTrack_DFS() {

        Stack<Node> stack = new Stack<>();

        Node current = nodes[0][0];
        current.visited = true;

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

    public boolean inBound(int x, int y) {
        return 0 <= x && x < nodes[0].length && 0 <= y && y < nodes.length;
    }
}
