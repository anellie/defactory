package xyz.angm.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import xyz.angm.game.world.entities.Player;

public class PlayerInputProcessor extends InputAdapter { // ToDo Map Inputs

    private final GameScreen screen;

    PlayerInputProcessor(GameScreen screen) {
        super();
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.A: // Left
                break;
            case Input.Keys.D: // Right
                break;
            case Input.Keys.W: // UP
                break;
            case Input.Keys.S: // Down
                break;
            case Input.Keys.E: // Inventory
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Input.Keys.A: // Left
                break;
            case Input.Keys.D: // Right
                break;
            case Input.Keys.W: // UP
                break;
            case Input.Keys.S: // Down
                break;
            case Input.Keys.E: // Inventory
                break;
        }
        return false;
    }
}
