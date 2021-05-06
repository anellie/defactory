package xyz.angm.game.ui.screens;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import xyz.angm.game.Defactory;
import xyz.angm.game.ui.Localization;

/** The first screen to be displayed; loads all assets. Will switch to {@link MenuScreen} on completion. */
public class AssetLoadingScreen extends Screen {

    private final VisProgressBar progressBar = new VisProgressBar(0f, 100f, 0.1f, false);

    /** Constructs the screen to start loading assets from the game's asset manager.
     * @param game The game the screen is running under. */
    public AssetLoadingScreen(Defactory game) {
        super(game);
        table.add(new VisLabel(Localization.get("loadingAssets"))).row();
        table.add(progressBar).size(500f, 10f);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Defactory.assets.update();
        progressBar.setValue(Defactory.assets.getProgress() * 100f);

        if (Defactory.assets.isFinished()) game.setScreen(new MenuScreen(game));
    }
}
