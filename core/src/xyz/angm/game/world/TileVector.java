package xyz.angm.game.world;

import com.badlogic.gdx.math.Vector2;

import static xyz.angm.game.world.WorldMap.TILE_SIZE;

/** A 2D vector using integers for storing its values. Used for storing positions of blocks in the world map. */
class TileVector {

    /** The first axis of the vector. */
    private int x;
    /** The second axis of the vector. */
    private int y;

    /** Creates a vector at (0, 0). */
    TileVector() {
        this(0, 0);
    }

    /** Creates a vector with the given parameters.
     * @param x The first axis.
     * @param y The second axis. */
    TileVector(int x, int y) {
        this.x = x;
        this.y = y;
    }

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
    public TileVector set(int x, int y) {
        // Ensure the vector stays in the tile grid
        this.x = (x / TILE_SIZE) * TILE_SIZE;
        this.y = (y / TILE_SIZE) * TILE_SIZE;
        return this;
    }

    /** Sets from the given vector.
     * @param v Applies values from this vector.
     * @return Itself. */
    public TileVector set(TileVector v) {
        return set(v.x, v.y);
    }

    /** Sets from the given vector. Both axes are floored.
     * @param v Applies values from this vector.
     * @return Itself. */
    public TileVector set(Vector2 v) {
        return set((int) v.x, (int) v.y);
    }
}
