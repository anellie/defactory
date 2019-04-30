package xyz.angm.game.world;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** A class containing all map data:
 * - Texture data for rendering the map
 * - Tile data for getting the terrain type (TODO). */
class WorldMap extends Image {

    /** Constructs a map; generating its content during construction.
     * @param generator The world generator to obtain data from. */
    WorldMap(TerrainGenerator generator) {
        super(generator.createWorldMapTexture());
        setPosition(0f, 0f);
    }
}
