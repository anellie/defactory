package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import static xyz.angm.game.ui.Screen.BUTTON_HEIGHT;
import static xyz.angm.game.ui.Screen.BUTTON_WIDTH;

/** A pause panel displayed when pressing escape. */
class PausePanel extends VisTable {

    /** Construct a new panel. */
    PausePanel() {
        setFillParent(true);
        setBackground(VisUI.getSkin().getDrawable("black-transparent"));

        add(new VisLabel("Pause Menu")).row();

        VisTextButton exitGameButton = new VisTextButton("Exit Game");
        add(exitGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();

        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }
}

