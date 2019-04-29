package xyz.angm.game;

import com.badlogic.gdx.assets.AssetManager;
import com.kotcrab.vis.ui.VisUI;
import xyz.angm.game.network.Client;
import xyz.angm.game.network.NetworkInterface;
import xyz.angm.game.network.Server;
import xyz.angm.game.ui.MenuScreen;

/** The main class of the game. */
public class Game extends com.badlogic.gdx.Game {

	private final AssetManager assets = new AssetManager();
	private NetworkInterface netIface;

	@Override
	public void create() {
		VisUI.load(); // VisUI is a framework for game GUIs like menus
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		assets.dispose();
	}

	/** Starts a game as the player. Will create a server for players playing as beasts to join.*/
	public void startGame() {
		netIface = new Server();
		netIface.start();
		// TODO game screen
	}

	/** Joins a server. Allows the player to play as a beast trying to destroy the base. */
	public void joinGame() {
		netIface = new Client();
		netIface.start();
		// TODO game screen
	}
}
