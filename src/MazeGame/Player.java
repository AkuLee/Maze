package MazeGame;

public class Player {

    private int x, y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveUp() {
        this.y = this.y - 1;
    }

    public void moveDown() {
        this.y = this.y + 1;
    }

    public void moveLeft() {
        this.x = this.x - 1;
    }

    public void moveRight() {
        this.x = this.x + 1;
    }
}
