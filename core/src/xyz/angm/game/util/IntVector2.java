package xyz.angm.game.util;

/** A 2D vector using integers for storing it's values. */
public class IntVector2 {

    /** The first axis of the vector. */
    public int x;
    /** The second axis of the vector. */
    public int y;

    /** Creates a vector at (0, 0). */
    public IntVector2() {
        this(0, 0);
    }

    /** Creates a vector with the given parameters.
     * @param x The first axis.
     * @param y The second axis. */
    public IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Sets from the given vector.
     * @param v Applies values from this vector.
     * @return Itself. */
    public IntVector2 set(IntVector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    /** Adds the given vector.
     * @param v Applies values from this vector.
     * @return Itself. */
    public IntVector2 add(IntVector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    /** Multiplies both axes with the given value.
     * @param num Axes are multiplied with this value.
     * @return Itself. */
    public IntVector2 multiply(float num) {
        x *= num;
        y *= num;
        return this;
    }
}
