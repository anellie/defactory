package xyz.angm.game;

import com.badlogic.gdx.assets.AssetManager;
import com.kotcrab.vis.ui.VisUI;
import xyz.angm.game.ui.MenuScreen;

/** The main class of the game. */
public class Game extends com.badlogic.gdx.Game {

	private final AssetManager assets = new AssetManager();

	@Override
	public void create () {
		VisUI.load(); // VisUI is a framework for game GUIs like menus
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose () {
		assets.dispose();
	}
}
