package xyz.angm.game.ui;

import xyz.angm.game.Game;
import xyz.angm.game.network.Client;
import xyz.angm.game.world.World;

/** The screen active while the game is running. */
public class GameScreen extends Screen {

    private World world;

    /** Constructs the screen and generates a new world. Run only when server is active.
     * @param game The game the screen is running under. */
    public GameScreen(Game game) {
        super(game);
        world = new World(System.currentTimeMillis());
        world.registerActors(stage);
    }

    /** Constructs the screen and waits for the world from the server.
     * @param game The game the screen is running under.
     * @param client The client to wait for events with. Client should not have connected yet! */
    public GameScreen(Game game, Client client) {
        super(game);
        client.addListener((Object obj) -> {
            if (obj instanceof Long) { // Long is the seed
                world = new World((Long) obj);
                world.registerActors(stage);
            }
        });
        client.start();
    }

    public World getWorld() {
        return world;
    }
}
