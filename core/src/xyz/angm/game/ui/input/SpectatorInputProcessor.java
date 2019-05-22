package xyz.angm.game.ui.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import xyz.angm.game.ui.screens.GameScreen;
import xyz.angm.game.world.World;

/** An input processor containing listeners specific to spectators. */
public class SpectatorInputProcessor extends InputProcessor {

    private final World world;
    private final Vector2 mousePosition = new Vector2();
    private final Vector2 tmpV = new Vector2();

    /** Create an input processor.
     * @param screen The screen to bind to */
    public SpectatorInputProcessor(GameScreen screen) {
        super(screen);
        this.world = screen.getWorld();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            world.requestBeastSpawn(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Set tmpV to the mouse position difference since the last mouse event
        tmpV.set(mousePosition).sub(screenX, screenY).scl(0.035f);
        tmpV.y = -tmpV.y; // Y axis has to be inverted since origins are different
        world.moveCamera(tmpV);
        mousePosition.set(screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePosition.set(screenX, screenY);
        return false;
    }
}
