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

    private final TileVector position = new TileVector();
    private final transient Image actor = new Image(Game.assets.get("textures/blockTest.png", Texture.class));

    /** Required for kryo deserialization. */
    private Block() {
        actor.setSize(BLOCK_SIZE, BLOCK_SIZE);
    }

    /** Construct a new block at the specified position. Call registerToStage to display it.
     * @param position The position of the block. Actor position is also set with this. */
    Block(TileVector position) {
        this();
        this.position.set(position);
    }

    TileVector getPosition() {
        return position;
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    void registerToStage(Stage stage) {
        stage.addActor(actor);
        actor.setPosition(position.getX(), position.getY());
    }

    @Override
    public void dispose() {
        actor.remove();
    }

    /** The type of a block determines its capabilities and behavior. */
    public enum Type {
        /** A block with no special properties. */
        DEFAULT,
        /** A block capable of damaging enemies over a given range. */
        TURRET,
        /** A block capable of healing other blocks in a given range. */
        HEALER,
        /** A block vital to the game. If this block is destroyed, the player loses. */
        CORE
    }

    /** A class holding all (static) properties of a block. All block types (and their properties) are loaded from a JSON document on boot.
     * All properties are initialised to their defaults; this saves disk space since they do not need to be in the JSON document. */
    public class BlockProperties {

        /** Health of a block; eg how much enemy hits it can take. */
        public final int health = 1;
        /** The path to the blocks texture relative to '@/core/assets/textures'. */
        public final String texture = "";
        /** The type of the block. See Type enum. */
        public final Type type = Type.DEFAULT;

        /** TURRET specific: The interval in which the turret fires. */
        public final float turretFireRate = 1f;
        /** TURRET specific: The range of the turret. */
        public final float turretRange = 1f;
        /** TURRET specific: The damage of the turret per shot. */
        public final int turretDamage = 1;

        /** HEALER specific: The interval in which the healer heals surrounding blocks. */
        public final float healerRate = 1f;
        /** HEALER specific: The range of the healer. */
        public final float healerRange = 1f;
        /** HEALER specific: The damage recovered per heal cycle per block. */
        public final int healerRecovery = 1;

    }
}
