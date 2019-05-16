package xyz.angm.game.ui.screens;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import xyz.angm.game.Game;
import xyz.angm.game.ui.Localization;

/** The first screen to be displayed; loads all assets. Will switch to {@link MenuScreen} on completion. */
public class AssetLoadingScreen extends Screen {

    private final VisProgressBar progressBar = new VisProgressBar(0f, 100f, 0.1f, false);

    /** Constructs the screen to start loading assets from the game's asset manager.
     * @param game The game the screen is running under. */
    public AssetLoadingScreen(Game game) {
        super(game);
        table.add(new VisLabel(Localization.get("loadingAssets"))).row();
        table.add(progressBar).size(500f, 10f);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Game.assets.update();
        progressBar.setValue(Game.assets.getProgress() * 100f);

        if (Game.assets.isFinished()) game.setScreen(new MenuScreen(game));
    }
}
