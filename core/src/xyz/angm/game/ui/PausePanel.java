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

    private final GameScreen screen;

    /** Construct a new panel. */
    PausePanel(GameScreen screen) {
        super();
        this.screen = screen;

        setFillParent(true);
        setBackground(VisUI.getSkin().getDrawable("black-transparent"));

        add(new VisLabel("Pause Menu")).row();

        VisTextButton resumeGameButton = new VisTextButton("Resume Game");
        VisTextButton gameMainMenu = new VisTextButton("MainMenu");
        VisTextButton exitGameButton = new VisTextButton("Exit Game");

        resumeGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.togglePausePanel();
            }
        });
        gameMainMenu.addListener(new ClickListener() {
            @Override
            public  void clicked(InputEvent event, float x, float y) {
                screen.backToMainMenu();
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
        add(exitGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
    }
}

