package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import static xyz.angm.game.ui.Screen.BUTTON_HEIGHT;
import static xyz.angm.game.ui.Screen.BUTTON_WIDTH;

/** A pause panel displayed when pressing escape. */
class PausePanel extends VisTable {

    /** Construct a new panel.
     * @param screen Screen the panel will be displayed in */
    PausePanel(GameScreen screen) {
        super(true);

        setFillParent(true);
        setBackground(VisUI.getSkin().getDrawable("black-transparent"));

        add(new VisLabel(Localization.get("pauseMenuTitle"))).padBottom(BUTTON_HEIGHT).row();

        VisTextButton resumeGameButton = new VisTextButton(Localization.get("pauseMenuResume"));
        VisTextButton gameMainMenu = new VisTextButton(Localization.get("backToMain"));
        VisTextButton exitGameButton = new VisTextButton(Localization.get("exitButton"));

        VisSelectBox<Localization.Language> languageDropdown = new VisSelectBox<>();
        languageDropdown.setItems(Localization.Language.values());
        languageDropdown.setSelected(Localization.getCurrentLocale());
        languageDropdown.setAlignment(Align.center);

        resumeGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.togglePausePanel();
            }
        });
        gameMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.returnToMainMenu();
            }
        });
        languageDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Localization.setLocale(languageDropdown.getSelected());
                screen.localeChanged();
            }
        });
        exitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        add(resumeGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
        add(gameMainMenu).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
        add(languageDropdown).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
        add(exitGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
    }
}

