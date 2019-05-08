package xyz.angm.game.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import static xyz.angm.game.ui.Screen.BUTTON_HEIGHT;
import static xyz.angm.game.ui.Screen.BUTTON_WIDTH;

public class PausePanel extends VisTable {

     public PausePanel(){
         setFillParent(true);
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

