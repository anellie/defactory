package xyz.angm.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import xyz.angm.game.Game;

/** A block can be placed by the player, onto a tile in the world map.
 * Different block types have different function. */
public class Block implements Disposable {

    private int type;
    private final TileVector position = new TileVector();
    private Direction direction;
    private transient Image actor;

    /** Required for kryo deserialization. */
    private Block() {}

    /** Construct a new block at the specified position. Call registerToStage to display it.
     * @param position The position of the block. Actor position is also set with this.
     * @param type The type of this block.
     * @param direction The direction the block is facing. */
    Block(TileVector position, int type, Direction direction) {
        this();
        this.position.set(position);
        this.type = type;
        this.direction = direction;
    }

    TileVector getPosition() {
        return position;
    }

    Direction getDirection() {
        return direction;
    }

    BlockProperties getProperties() {
        return BlockProperties.getProperties(type);
    }

    /** Adds itself to the given group.
     * @param group Group to be added to */
    void registerToGroup(Group group) {
        if (actor == null) actor = new Image(Game.assets.get(getProperties().getFullTexturePath(), Texture.class));
        group.addActor(actor);
        actor.setSize(1, 1);
        actor.setOrigin(Align.center);
        actor.setPosition(position.getX(), position.getY());
        actor.setRotation(Block.directionToDegrees(direction));
    }

    @Override
    public void dispose() {
        if (actor != null) actor.remove();
    }

    /** The direction a block can be facing. Needed by some blocks; eg conveyor belts. */
    @SuppressWarnings("JavaDoc")
    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    /** Convert directions to degrees.
     * @param d The direction to convert
     * @return The direction in degrees; right = 0deg; counter-clockwise. */
    static int directionToDegrees(Direction d) {
        if (d == Direction.UP) return 90;
        else if (d == Direction.RIGHT) return 0;
        else if (d == Direction.DOWN) return 270;
        else return 180;
    }
}
