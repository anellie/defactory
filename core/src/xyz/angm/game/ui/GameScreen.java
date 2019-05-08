package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
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

    private World world = new World(System.currentTimeMillis());
    private PlayerHud playerHud = new PlayerHud(this);

    /** Constructs the screen and generates a new world. Run only when server is active.
     * @param game The game the screen is running under. */
    public GameScreen(Game game) {
        super(game);
        stage.addActor(playerHud);

        // Create a multiplexer for handling input for both UI and in-world (https://github.com/libgdx/libgdx/wiki/Event-handling#inputmultiplexer)
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new PlayerInputProcessor(this));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /** Constructs the screen and waits for the world from the server.
     * @param game The game the screen is running under.
     * @param client The client to wait for events with. Client should not have connected yet! */
    public GameScreen(Game game, Client client) {
        super(game);
        client.addListener(this::serverPacketReceived);

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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (world == null) return;  // Waiting for server connect
        world.act(delta);           // Update world
        world.render(delta);        // Render world. World render is separate to allow for different camera positions

        stage.act(delta);
        stage.draw();
    }

    // Packet/Object received from server. Only call on client instances.
    private void serverPacketReceived(Object packet) {
        if (packet instanceof Long) {           // Long is the seed; world needs to init now
            Gdx.app.postRunnable(() -> world = new World((Long) packet)); // World requires render context
        } else if (packet instanceof Player) {  // Player should be synced
            Player serverPlayer = (Player) packet;
            Player localPlayer = world.getPlayer();
            localPlayer.getPosition().set(serverPlayer.getPosition());
        }
    }
}
