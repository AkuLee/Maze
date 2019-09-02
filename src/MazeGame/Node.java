package MazeGame;

public class Node {

    int length; // Length of display

    int x, y;
    boolean visited;

    // Used for a* and djisktra
    // gScore is distance from start and fScore is heuristic distance from start to end from this node.
    double fScore = Double.MAX_VALUE, gScore = Double.MAX_VALUE;

    boolean[] walls; // top right bottom left

    Node parent = null; // Node from which we came from to this node

    public Node(int length, int x, int y) {
        this.length = length;
        this.x = x;
        this.y = y;
        visited = false;
        walls = new boolean[] {true, true, true, true}; // top right bottom left
    }

    /**
     * Method will remove the wall between two neighbouring nodes.
     * @param n1 node 1
     * @param n2 node 2
     */
    public static void removeCommonWall(Node n1, Node n2) {
        int compX = n1.x - n2.x;
        int compY = n1.y - n2.y;

        if (compX == -1) { // Right of n1 and left of n2
            n1.walls[1] = false;
            n2.walls[3] = false;
        } else if (compX == 1) { // Left of n1 and right of n2
            n1.walls[3] = false;
            n2.walls[1] = false;
        } else if (compY == -1) { // bottom of n1 and top of n2
            n1.walls[2] = false;
            n2.walls[0] = false;
        } else if (compY == 1) { // top of n1 and bottom of n2
            n1.walls[0] = false;
            n2.walls[2] = false;
        }
    }
}
