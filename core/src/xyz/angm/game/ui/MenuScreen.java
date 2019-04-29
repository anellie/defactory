package xyz.angm.game.ui;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import xyz.angm.game.Game;

public class MenuScreen extends Screen {

    public MenuScreen(Game game) {
        super(game);

        VisTable table = new VisTable(true);
        stage.addActor(table);
        table.setFillParent(true);

        table.add(new VisLabel("Hello World!"));
    }
}
