package xyz.angm.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;

import static xyz.angm.game.world.TerrainGenerator.WORLD_SIZE_MULTIPLICATOR;
import static xyz.angm.game.world.World.WORLD_VIEWPORT_HEIGHT;
import static xyz.angm.game.world.World.WORLD_VIEWPORT_WIDTH;

/** A class containing all map data:
 * - Texture data for rendering the map
 * - Tile data for getting the terrain type (TODO)
 * - All blocks placed on the terrain.
 */
class WorldMap extends Image {

    private final HashMap<TileVector, Block> blocks = new HashMap<>();

    /** Constructs a map; generating its content during construction.
     * @param generator The world generator to obtain data from. */
    WorldMap(TerrainGenerator generator) {
        super();

        // Rendering needs to happen in OpenGL context/thread; else crash
        Gdx.app.postRunnable(() -> {
            setDrawable(new TextureRegionDrawable(new TextureRegion((generator.createWorldMapTexture()))));
            setSize(WORLD_VIEWPORT_WIDTH * WORLD_SIZE_MULTIPLICATOR, WORLD_VIEWPORT_HEIGHT * WORLD_SIZE_MULTIPLICATOR);
            setPosition(0f, 0f);
        });
    }

    /** Adds the block given to the list of blocks.
     * @param block Block to add
     * @return false when block already exists; true if placing it was successful */
    boolean addBlock(Block block) {
        if (blocks.containsKey(block.getPosition())) return false;
        blocks.put(block.getPosition(), block);
        return true;
    }

    /** Removes a block at the given position.
     * @param position The position to remove the block at. */
    void removeBlock(TileVector position) {
        Block block = blocks.get(position);
        if (block != null) block.dispose();
        blocks.remove(position);
    }
}
