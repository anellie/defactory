package xyz.angm.game.ui.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import xyz.angm.game.ui.screens.GameScreen;
import xyz.angm.game.world.World;

/** An input processor for handling inputs by the player. Does not handle UI. */
public class PlayerInputProcessor extends InputProcessor {

    private final World world;

    /** Create an input processor.
     * @param screen The screen to bind to */
    public PlayerInputProcessor(GameScreen screen) {
        super(screen);
        this.world = screen.getWorld();
    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        switch (keycode) {
            case Input.Keys.A: // Left
                world.getPlayer().getVelocity().x--;
                break;
            case Input.Keys.D: // Right
                world.getPlayer().getVelocity().x++;
                break;
            case Input.Keys.W: // Up
                world.getPlayer().getVelocity().y++;
                break;
            case Input.Keys.S: // Down
                world.getPlayer().getVelocity().y--;
                break;
            case Input.Keys.SHIFT_LEFT: // Sprint
                world.getPlayer().sprint(true);
                break;
            case Input.Keys.R: // Cycle direction of the block the player is placing
                world.getPlayer().cycleDirection();
                world.updateSelector(Gdx.input.getX(), Gdx.input.getY());
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A: // Left
                world.getPlayer().getVelocity().x++;
                break;
            case Input.Keys.D: // Right
                world.getPlayer().getVelocity().x--;
                break;
            case Input.Keys.W: // Up
                world.getPlayer().getVelocity().y--;
                break;
            case Input.Keys.S: // Down
                world.getPlayer().getVelocity().y++;
                break;
            case Input.Keys.SHIFT_LEFT: // Sprint
                world.getPlayer().sprint(false);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            world.mapClicked(screenX, screenY, (button == Input.Buttons.RIGHT));
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        world.updateSelector(screenX, screenY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            world.mapClicked(screenX, screenY, Gdx.input.isButtonPressed(Input.Buttons.RIGHT));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        world.updateSelector(screenX, screenY);
        return true;
    }
}
