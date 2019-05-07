package xyz.angm.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import xyz.angm.game.Game;
import xyz.angm.game.network.Client;
import xyz.angm.game.world.entities.Player;
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
            if (obj instanceof Long) { // Long is the seed; world needs to init now
                world = new World((Long) obj);
                world.registerActors(stage);
            } else if (obj instanceof Player) { // Player should be synced
                world.getPlayer().getPosition().set(((Player) obj).getPosition()); // get is why java causes 836 cancer cases per year
            }
        });

        boolean connected = client.start();
        if (!connected) {
            table.add(new VisLabel("No server was found!")).row();
            VisTextButton backButton = new VisTextButton("Return");
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new MenuScreen(game));
                }
            });
            table.add(backButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    public World getWorld() {
        return world;
    }
}
