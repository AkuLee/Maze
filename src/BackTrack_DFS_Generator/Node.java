package BackTrack_DFS_Generator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Node {

    int length;

    int x, y;
    boolean visited;

    boolean[] walls;

    public Node(int length, int x, int y) {
        this.length = length;
        this.x = x;
        this.y = y;
        visited = false;
        walls = new boolean[] {true, true, true, true}; // top right bottom left
    }

    public void show(GraphicsContext context, Color boxColor) {

        int thisX = this.x * length, thisY = this.y * length;

        context.setFill(boxColor);
        context.fillRect(thisX, thisY, length, length);

        context.setStroke(Color.rgb(65, 75, 125));
        if (walls[0]) { // top
            context.strokeLine(thisX, thisY, thisX + length, thisY);
        }

        if (walls[1]) { // right
            context.strokeLine(thisX + length, thisY, thisX + length, thisY + length);
        }

        if (walls[2]) { // bottom
            context.strokeLine(thisX + length, thisY + length, thisX, thisY + length);
        }

        if (walls[3]) { // left
            context.strokeLine(thisX, thisY + length, thisX, thisY);
        }

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
