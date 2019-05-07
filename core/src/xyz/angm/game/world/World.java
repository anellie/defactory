package xyz.angm.game.world;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import xyz.angm.game.world.entities.Player;

import static xyz.angm.game.ui.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.Screen.VIEWPORT_WIDTH;

/** Represents the game world and contains all entities and the world map. */
public class World {

    /** Seed used for generating terrain. See {@link TerrainGenerator}. */
    public final long seed;
    private final Stage stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
    private final WorldMap map;
    private Player player = new Player();

    /** Constructs a new world along with it's map.
     * @param seed The seed for world generation. */
    public World(long seed) {
        this.seed = seed;
        map = new WorldMap(new TerrainGenerator(seed));

        stage.addActor(map);
        player.registerToStage(stage);
    }

    /** Returns the player entity.
     * @return The player */
    public Player getPlayer() {
        return player;
    }

    /** Should be called every frame on the server so the world can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        player.act(delta);
    }

    /** Should be called every frame when the world should render itself and all components.
     * @param delta Time since last call to this method in seconds. */
    public void render(float delta) {
        stage.getCamera().position.set(player.getPosition().x, player.getPosition().y, 0);
        stage.draw();
    }
}
