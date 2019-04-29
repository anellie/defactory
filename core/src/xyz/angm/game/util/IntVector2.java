package xyz.angm.game.util;

public class IntVector2 {

    public int x;
    public int y;

    public IntVector2() {
        this(0, 0);
    }

    public IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IntVector2 copy() {
        return new IntVector2(this.x, this.y);
    }
}
