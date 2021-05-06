package xyz.angm.game.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import xyz.angm.game.Defactory;

/** An abstract class shared between all screens. */
public abstract class Screen extends ScreenAdapter {

    private static int viewportHeight = 1080;
    private static int viewportWidth = 1920;
    
    /** Height to be used for VisTextButton. */
    public static final float BUTTON_HEIGHT = 48f;
    /** Width to be used for VisTextButton. */
    public static final float BUTTON_WIDTH = 400f;

    /** The game the screen is running under. */
    final Defactory game;
    /** The stage containing all 2D actors. */
    final Stage stage = new Stage(new ScreenViewport());
    /** A table for GUI elements. Is empty by default, and part of the stage. */
    final VisTable table = new VisTable(true);

    /** Constructs an empty screen.
     * @param game The game the screen is running under. */
    Screen(Defactory game) {
        this.game = game;
        Gdx.input.setInputProcessor(stage);
        stage.addActor(table);
        table.setFillParent(true);
    }

    /** Height to be used for all viewports. */
    public static int getViewportHeight() {
        return viewportHeight;
    }

    /** Width to be used for all viewports. */
    public static int getViewportWidth() {
        return viewportWidth;
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
        viewportHeight = stage.getViewport().getScreenHeight();
        viewportWidth = stage.getViewport().getScreenWidth();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
