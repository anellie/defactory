package xyz.angm.game.ui.screens;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import xyz.angm.game.Game;
import xyz.angm.game.ui.Localization;
import xyz.angm.game.world.TerrainGenerator;
import xyz.angm.game.world.World;

/** The screen displayed while generating the map. Switches to {@link GameScreen} when finished. */
public class MapLoadingScreen extends Screen {

    private final VisProgressBar progressBar = new VisProgressBar(0f, 100f, 0.1f, false);
    private final TerrainGenerator generator;

    /** Constructs the screen to start generating the map.
     * @param game The game the screen is running under.
     * @param seed The seed to generate with. */
    public MapLoadingScreen(Game game, long seed) {
        super(game);
        table.add(new VisLabel(Localization.get("loadingMap"))).row();
        table.add(progressBar).size(500f, 10f);
        generator = new TerrainGenerator(seed);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        float progress = generator.continueLoading();
        progressBar.setValue(progress * 100f);

        if (progress == 1f) game.setScreen(new GameScreen(game, new World(generator, game.isServer())));
    }
}
