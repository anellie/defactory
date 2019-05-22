package xyz.angm.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import xyz.angm.game.Game;
import xyz.angm.game.network.Client;
import xyz.angm.game.ui.PausePanel;
import xyz.angm.game.ui.PlayerHud;
import xyz.angm.game.ui.SpectatorHud;
import xyz.angm.game.ui.input.PlayerInputProcessor;
import xyz.angm.game.ui.input.SpectatorInputProcessor;
import xyz.angm.game.world.TileVector;
import xyz.angm.game.world.World;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.entities.Beast;
import xyz.angm.game.world.entities.Player;

/** The screen active while the game is running. */
public class GameScreen extends Screen {

    private World world;
    private PlayerHud hud;
    private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private boolean pauseMenuActive = false;

    /** Constructs the screen. Automatically determines if player or spectator.
     * @param game The game the screen is running under.
     * @param world The world to use. */
    GameScreen(Game game, World world) {
        super(game);
        this.world = world;
        this.hud = game.isServer() ? new PlayerHud(this) : new SpectatorHud(this);

        stage.addActor(hud);
        if (!game.isServer()) game.getClient().addListener(this::serverPacketReceived);

        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(game.isServer() ? new PlayerInputProcessor(this) : new SpectatorInputProcessor(this));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (world.getPlayer().getCore().getHealth() < 0) onGameLoss();

        world.act(delta);
        world.render();     // World render is separate to allow for different camera positions.

        stage.act(delta);
        stage.draw();
    }

    // Called when a packet/object was received from the server. Only call on client instances. TODO packets.
    private void serverPacketReceived(Object packet) {
        if (packet instanceof Player) {  // Player should be synced
            Player serverPlayer = (Player) packet;
            Player localPlayer = world.getPlayer();
            localPlayer.getPosition().set(serverPlayer.getPosition());
        }
        else if (packet == Client.Status.DISCONNECTED) { // Disconnect from server
            Gdx.app.postRunnable(this::onServerDisconnect); // Disposing game screen requires render context
        }
        else if (packet instanceof Block) { // Block should be placed
            world.addBlock((Block) packet);
        }
        else if (packet instanceof TileVector) { // Block should removed
            world.removeBlock((TileVector) packet);
        }
        else if (packet instanceof Beast) { // Beast was spawned or killed
            Beast beast = (Beast) packet;
            if (beast.getHealth() <= 0) world.removeBeast(beast);
            else world.addBeast(beast);
        }
        else if (packet instanceof Array) { // Beast positions
            world.updateBeastPositions((Array<Vector2>) packet);
        }
        else if (packet == Client.Status.WAVE_START || packet == Client.Status.WAVE_END) {
            getWorld().waveStatusChanged((Client.Status) packet);
        }
    }

    /** Toggles the pause menu. */
    public void togglePausePanel() {
        stage.clear();
        if (!pauseMenuActive) { // Open the pause menu
            stage.addActor(new PausePanel(this));
            Gdx.input.setInputProcessor(stage);
        } else { // Close the pause menu
            stage.addActor(hud);
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
        pauseMenuActive = !pauseMenuActive;
    }

    /** Go back to the main menu. */
    public void returnToMainMenu() {
        dispose();
        game.setScreen(new MenuScreen(game));
    }

    // Called when the CORE was destroyed
    private void onGameLoss() {
        dispose();
        game.setScreen(new MessageScreen(game, "gameLost"));
    }
    // Called on client when server disconnects
    private void onServerDisconnect() {
        dispose();
        game.setScreen(new MessageScreen(game, "serverDisconnect"));
    }

    /** Called when the locale changed. Reloads all UI. */
    public void localeChanged() {
        stage.clear();
        hud.reload();
        if (pauseMenuActive) stage.addActor(new PausePanel(this));
        else stage.addActor(hud);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (world != null) world.resizeViewport(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        world.dispose();
        game.disposeNetworkInterface();
    }

    /** Call when a wave of beasts has begun. */
    public void waveBegun() {
        world.getPlayer().nextWave();
    }
}
