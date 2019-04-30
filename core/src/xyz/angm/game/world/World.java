package xyz.angm.game.world;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** Represents the game world and contains all entities and the world map. */
public class World {

    private final TerrainGenerator generator = new TerrainGenerator();
    private final WorldMap map = new WorldMap(generator);

    /** Adds all actors contained in the world to the stage.
     * @param stage The stage used for rendering */
    public void registerActors(Stage stage) {
        stage.addActor(map);
    }
}
