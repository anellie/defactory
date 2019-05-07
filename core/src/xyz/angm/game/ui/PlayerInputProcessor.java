package xyz.angm.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/** An input processor for handling inputs by the player. Does not handle UI. */
class PlayerInputProcessor extends InputAdapter {

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
            default:
                break;
        }
        return true;
    }
}
