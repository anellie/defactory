package xyz.angm.game.world.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import xyz.angm.game.Game;
import xyz.angm.game.world.TileVector;

/** A block can be placed by the player, onto a tile in the world map.
 * Different block implementations have different function. */
public class Block implements Disposable {

    /** Size of a blocks actor in pixels. */
    private static final int BLOCK_SIZE = 16;

    private final TileVector position;
    private final transient Image actor = new Image(Game.assets.get("textures/blockTest.png", Texture.class));

    /** Construct a new block at the specified position. Call registerToStage to display it.
     * @param position The position of the block. Actor position is also set with this. */
    public Block(TileVector position) {
        this.position = position;
        actor.setSize(BLOCK_SIZE, BLOCK_SIZE);
    }

    public TileVector getPosition() {
        return position;
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    public void registerToStage(Stage stage) {
        stage.addActor(actor);
        actor.setPosition(position.getX(), position.getY());
    }

    @Override
    public void dispose() {
        actor.remove();
    }
}
