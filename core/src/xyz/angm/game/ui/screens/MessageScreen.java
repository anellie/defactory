package xyz.angm.game.ui.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import xyz.angm.game.Defactory;
import xyz.angm.game.ui.Localization;

/** A screen displaying a message. Will include a button to return to {@link MenuScreen}. */
public class MessageScreen extends Screen {

    /** Constructs the screen to display the message.
     * @param game The game the screen is running under.
     * @param message The message to display. */
    public MessageScreen(Defactory game, String message) {
        super(game);
        table.add(new VisLabel(Localization.get(message))).row();

        VisTextButton returnButton = new VisTextButton(Localization.get("returnToMainMenu"));
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        table.add(returnButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
    }
}

