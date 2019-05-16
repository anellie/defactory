package xyz.angm.game.ui.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import xyz.angm.game.ui.screens.GameScreen;

/** An input processor for handling inputs by both player and spectator. Does not handle UI. */
public class InputProcessor extends InputAdapter {

    /** Scaling of map zoom caused by the mouse wheel. */
    private static final float SCROLL_SCALING = 0.01f;

    private final GameScreen screen;

    /** Create an input processor.
     * @param screen The screen to bind to */
    InputProcessor(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) { // Pause Menu
            screen.togglePausePanel();
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        float scrolled = (float) amount * SCROLL_SCALING;
        screen.getWorld().zoomMap(scrolled);
        return true;
    }
}
