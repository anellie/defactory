package xyz.angm.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.kotcrab.vis.ui.VisUI;
import xyz.angm.game.network.Client;
import xyz.angm.game.network.NetworkInterface;
import xyz.angm.game.network.Server;
import xyz.angm.game.ui.GameScreen;
import xyz.angm.game.ui.LoadingScreen;

/** The main class of the game. */
public class Game extends com.badlogic.gdx.Game {

    /** Static since loading texture more than once is pointless. Also prevents passing textures around everywhere. */
    @SuppressWarnings("LibGDXStaticResource") // Only applies to Android
    public static final AssetManager assets = new AssetManager();

    private NetworkInterface netIface;

    @Override
    public void create() {
        VisUI.load(); // VisUI is a framework for game GUIs like menus
        registerAllAssets();
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

    /** Starts a game as the player. Will create a server for players playing as beasts to join.*/
    public void startGame() {
        netIface = new Server(this);
        netIface.start();
        setScreen(new GameScreen(this));
    }

    /** Joins a server. Allows the player to play as a beast trying to destroy the base. */
    public void joinGame() {
        netIface = new Client();
        setScreen(new GameScreen(this, (Client) netIface));
    }

    private void registerAllAssets() {
        assets.load("textures/player.png", Texture.class);
    }
}
