package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import xyz.angm.game.Game;

/** The menu screen. */
class MenuScreen extends Screen {

    /** Constructs the screen with the main menu active.
     * @param game The game the screen is running under. */
    MenuScreen(Game game) {
        super(game);

        table.add(new VisLabel(Localization.get("gameName"))).padBottom(BUTTON_HEIGHT).row();

        VisTextButton startGameButton = new VisTextButton(Localization.get("mainMenuStart"));
        VisTextButton joinGameButton = new VisTextButton(Localization.get("mainMenuJoin"));
        VisTextButton changeGameLanguage = new VisTextButton(Localization.get("changeLang"));
        VisTextButton exitGameButton = new VisTextButton(Localization.get("exitButton"));

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startGame();
            }
        });
        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.joinGame();
            }
        });
        changeGameLanguage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Localization.cycleLocale();
                game.setScreen(new MenuScreen(game));
            }
        });
        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(startGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
        table.add(joinGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
        table.add(changeGameLanguage).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
        table.add(exitGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
    }
}
