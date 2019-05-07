package xyz.angm.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class PlayerInputProcessor extends InputAdapter { //ToDo Map Inputs
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.A: //Left
                break;

            case Input.Keys.D: //Right
                break;

            case Input.Keys.W: //UP
                break;

            case Input.Keys.S: //Down
                break;

            case Input.Keys.E: //Inventory
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode){
            case Input.Keys.A: //Left
                break;

            case Input.Keys.D: //Right
                break;

            case Input.Keys.W: //UP
                break;

            case Input.Keys.S: //Down
                break;

            case Input.Keys.E: //Inventory
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
