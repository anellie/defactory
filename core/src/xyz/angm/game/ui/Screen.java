package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import xyz.angm.game.Game;

/** An abstract class shared between all screens. */
abstract class Screen extends ScreenAdapter {

    /** The game the screen is running under. */
    final Game game;
    /** The stage containing all 2D actors. */
    final Stage stage = new Stage(new FitViewport(1920, 1080));

    /** Constructs an empty screen.
     * @param game The game the screen is running under. */
    Screen(Game game) {
        this.game = game;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
