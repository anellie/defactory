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

    private final TileVector position;
    private int hp;
    private final transient Image actor = new Image(Game.assets.get("textures/blockTest.png", Texture.class));

    public Block(TileVector position) {
        this.position = position;
        actor.setSize(16, 16);
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
