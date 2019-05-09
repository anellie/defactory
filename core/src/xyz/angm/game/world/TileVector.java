package xyz.angm.game.world;

import com.badlogic.gdx.math.Vector2;

import static xyz.angm.game.world.WorldMap.TILE_SIZE;

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
        // Ensure the vector stays in the tile grid
        this.x = (x / TILE_SIZE) * TILE_SIZE;
        this.y = (y / TILE_SIZE) * TILE_SIZE;
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
