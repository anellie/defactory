package xyz.angm.game.world;

import com.badlogic.gdx.math.Vector2;

/** A 2D vector using integers for storing its values. Used for storing positions of blocks in the world map. */
public class TileVector {

    /** The first axis of the vector. */
    private int x = 0;
    /** The second axis of the vector. */
    private int y = 0;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /** Sets from the given values.
     * @param x The first axis.
     * @param y The second axis.
     * @return Itself. */
    private TileVector set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /** Sets from the given vector. Both axes are floored.
     * @param v Applies values from this vector.
     * @return Itself. */
    public TileVector set(Vector2 v) {
        return set((int) v.x, (int) v.y);
    }

    /** Sets from the given vector.
     * @param v Applies values from this vector.
     * @return Itself. */
    public TileVector set(TileVector v) {
        return set(v.x, v.y);
    }

    /** Adds the values to itself.
     * @param x Added to the X axis.
     * @param y Added to the Y axis.
     * @return Itself. */
    public TileVector add(int x, int y) {
        return set(this.getX() + x, this.getY() + y);
    }

    /** Returns true if both axes match. */
    @Override
    public boolean equals(Object o) {
        return (o instanceof TileVector) && x == ((TileVector) o).getX() && y == ((TileVector) o).getY();
    }

    @Override
    public int hashCode() {
        return x + y;
    }
}
