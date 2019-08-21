package MazeGame;

public class Node {

    int length;

    int x, y;
    boolean visited;

    // gScore is distance from start and fScore is heuristic distance from start to end from this node.
    double fScore = Double.MAX_VALUE, gScore = Double.MAX_VALUE;

    boolean[] walls;

    Node parent = null;

    public Node(int length, int x, int y) {
        this.length = length;
        this.x = x;
        this.y = y;
        visited = false;
        walls = new boolean[] {true, true, true, true}; // top right bottom left
    }

    public static void removeCommonWall(Node n1, Node n2) {
        int compX = n1.x - n2.x;
        int compY = n1.y - n2.y;

        if (compX == -1) {
            n1.walls[1] = false;
            n2.walls[3] = false;
        } else if (compX == 1) {
            n1.walls[3] = false;
            n2.walls[1] = false;
        } else if (compY == -1) {
            n1.walls[2] = false;
            n2.walls[0] = false;
        } else if (compY == 1) {
            n1.walls[0] = false;
            n2.walls[2] = false;
        }
    }
}
