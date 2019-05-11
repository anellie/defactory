package xyz.angm.game.ui;

import com.badlogic.gdx.math.Vector2;

public class SpectatorInputProcessor extends InputProcessor {

    private final GameScreen screen;
    private final Vector2 mousePosition = new Vector2();
    private final Vector2 tmpV = new Vector2();

    /** Create an input processor.
     * @param screen The screen to bind to */
    SpectatorInputProcessor(GameScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        tmpV.set(mousePosition).sub(screenX, screenY);
        tmpV.y = -tmpV.y; // Y axis has to be inverted since origins are different
        screen.getWorld().moveCamera(tmpV);
        mousePosition.set(screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set(screenX, screenY);
        return false;
    }
}
