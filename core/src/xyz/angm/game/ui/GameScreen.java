package xyz.angm.game.ui;

import xyz.angm.game.Game;
import xyz.angm.game.world.World;

/** The screen active while the game is running. */
public class GameScreen extends Screen {

    private World world = new World();

    /** Constructs the screen and generates a new world.
     * @param game The game the screen is running under. */
    public GameScreen(Game game) {
        super(game);
        world.registerActors(stage);
    }
}
