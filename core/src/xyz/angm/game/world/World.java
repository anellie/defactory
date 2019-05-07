package xyz.angm.game.world;

import com.badlogic.gdx.scenes.scene2d.Stage;
import xyz.angm.game.world.entities.Player;

/** Represents the game world and contains all entities and the world map. */
public class World {

    public final long seed;
    private final WorldMap map;
    private Player player = new Player();

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
        player.registerToStage(stage);
    }

    /** Returns the player entity.
     * @return The player */
    public Player getPlayer() {
        return player;
    }
}
