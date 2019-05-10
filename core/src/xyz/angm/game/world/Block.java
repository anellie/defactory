package xyz.angm.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import xyz.angm.game.Game;

/** A block can be placed by the player, onto a tile in the world map.
 * Different block types have different function. */
public class Block implements Disposable {

    /** Size of a block actor in pixels. */
    private static final int BLOCK_SIZE = 16;

    private int type;
    private final TileVector position = new TileVector();
    private transient Image actor;

    /** Required for kryo deserialization. */
    private Block() {}

    /** Construct a new block at the specified position. Call registerToStage to display it.
     * @param position The position of the block. Actor position is also set with this.
     * @param type The type of this block. */
    Block(TileVector position, int type) {
        this();
        this.position.set(position);
        this.type = type;
    }

    TileVector getPosition() {
        return position;
    }

    private BlockProperties getProperties() {
        return BlockProperties.getProperties(type);
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    void registerToStage(Stage stage) {
        if (actor == null) actor = new Image(Game.assets.get(getProperties().getFullTexturePath(), Texture.class));
        stage.addActor(actor);
        actor.setSize(BLOCK_SIZE, BLOCK_SIZE);
        actor.setPosition(position.getX(), position.getY());
    }

    @Override
    public void dispose() {
        if (actor != null) actor.remove();
    }
}
