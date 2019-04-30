package xyz.angm.game.world;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** Represents the game world and contains all entities and the world map. */
public class World {

    public final long seed;
    private final WorldMap map;

    /** Constructs a new world along with it's map.
     * @param seed The seed for world generation. */
    public World(long seed) {
        this.seed = seed;
        map = new WorldMap(new TerrainGenerator(seed));
    }

    /** Adds all actors contained in the world to the stage.
     * @param stage The stage used for rendering */
    public void registerActors(Stage stage) {
        stage.addActor(map);
    }
}
