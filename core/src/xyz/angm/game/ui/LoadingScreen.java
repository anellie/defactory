package xyz.angm.game.ui;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import xyz.angm.game.Game;

/** The first screen to be displayed; load all assets. */
public class LoadingScreen extends Screen {

    private final VisProgressBar progressBar = new VisProgressBar(0f, 100f, 0.1f, false);

    /** Constructs the screen to start loading assets from the game's asset manager.
     * @param game The game the screen is running under. */
    public LoadingScreen(Game game) {
        super(game);
        table.add(new VisLabel("Loading assets!")).row();
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
