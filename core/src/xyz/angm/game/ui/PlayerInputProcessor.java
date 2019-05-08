package xyz.angm.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/** An input processor for handling inputs by the player. Does not handle UI. */
class PlayerInputProcessor extends InputAdapter {

    private static final float SCROLL_SCALING = 0.01f;

    private final GameScreen screen;

    /** Create an input processor.
     * @param screen The screen to bind to */
    PlayerInputProcessor(GameScreen screen) {
        super();
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A: // Left
                screen.getWorld().getPlayer().getVelocity().x--;
                break;
            case Input.Keys.D: // Right
                screen.getWorld().getPlayer().getVelocity().x++;
                break;
            case Input.Keys.W: // UP
                screen.getWorld().getPlayer().getVelocity().y++;
                break;
            case Input.Keys.S: // Down
                screen.getWorld().getPlayer().getVelocity().y--;
                break;
            case Input.Keys.E: // Inventory TODO
                break;
            case Input.Keys.SHIFT_LEFT: // Sprint
                screen.getWorld().getPlayer().sprint(true);
                break;
            case Input.Keys.ESCAPE: // Pause Menu
                screen.togglePausePanel();
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
                screen.getWorld().getPlayer().getVelocity().x++;
                break;
            case Input.Keys.D: // Right
                screen.getWorld().getPlayer().getVelocity().x--;
                break;
            case Input.Keys.W: // UP
                screen.getWorld().getPlayer().getVelocity().y--;
                break;
            case Input.Keys.S: // Down
                screen.getWorld().getPlayer().getVelocity().y++;
                break;
            case Input.Keys.E: // Inventory TODO
                break;
            case Input.Keys.SHIFT_LEFT: // Sprint
                screen.getWorld().getPlayer().sprint(false);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            screen.getWorld().mapClicked(screenX, screenY, (button == Input.Buttons.RIGHT));
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        float scrolled = (float) amount * SCROLL_SCALING;
        screen.getWorld().zoomMap(scrolled);
        return true;
    }
}
