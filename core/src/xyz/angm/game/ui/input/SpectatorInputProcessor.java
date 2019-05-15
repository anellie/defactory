package xyz.angm.game.ui.input;

import com.badlogic.gdx.math.Vector2;
import xyz.angm.game.ui.screens.GameScreen;

/** An input processor containing listeners specific to spectators/NPCs. */
public class SpectatorInputProcessor extends InputProcessor {

    private final GameScreen screen;
    private final Vector2 mousePosition = new Vector2();
    private final Vector2 tmpV = new Vector2();

    /** Create an input processor.
     * @param screen The screen to bind to */
    public SpectatorInputProcessor(GameScreen screen) {
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
