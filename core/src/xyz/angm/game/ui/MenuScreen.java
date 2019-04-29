package xyz.angm.game.ui;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import xyz.angm.game.Game;

/** The menu screen. */
public class MenuScreen extends Screen {

    /** Constructs the screen with the main menu active.
     * @param game The game the screen is running under. */
    public MenuScreen(Game game) {
        super(game);

        VisTable table = new VisTable(true);
        stage.addActor(table);
        table.setFillParent(true);

        table.add(new VisLabel("Hello World!"));
    }
}
