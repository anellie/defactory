package xyz.angm.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.angm.game.Game;
import xyz.angm.game.world.BlockProperties;

import static xyz.angm.game.ui.Screen.VIEWPORT_WIDTH;
import static xyz.angm.game.world.entities.Player.PLAYER_HEALTH;
import static xyz.angm.game.world.entities.Player.PLAYER_STAMINA;

/** The player HUD containing all GUI elements of the player, like health bar or inventory. */
class PlayerHud extends Group {

    private static final int BAR_WIDTH = 400;
    private static final int BAR_HEIGHT = 15;

    private final GameScreen screen;
    private final VisProgressBar healthBar = new VisProgressBar(0, PLAYER_HEALTH, 1, false, "health-bar");
    private final VisProgressBar staminaBar =
            new VisProgressBar(0, PLAYER_STAMINA, PLAYER_STAMINA / BAR_WIDTH, false, "stamina-bar");

    /** Construct a new HUD.
     * @param screen The screen the HUD will be a part of */
    PlayerHud(GameScreen screen) {
        super();
        this.screen = screen;

        // Window containing health + stamina bar
        VisWindow barWindow = new VisWindow("Status");
        barWindow.add(healthBar).size(BAR_WIDTH, BAR_HEIGHT).row();
        barWindow.add(staminaBar).size(BAR_WIDTH, BAR_HEIGHT);
        barWindow.pack();
        addActor(barWindow);

        // Window containing a selection of blocks the player can build
        VisWindow buildWindow = new VisWindow("Build");
        GridGroup buildSelectionContainer = new GridGroup(32, 4);
        ButtonGroup<VisImageButton> buildSelectionButtons = new ButtonGroup<>();
        buildSelectionButtons.setMinCheckCount(1);
        buildSelectionButtons.setMaxCheckCount(1);

        for (BlockProperties properties : BlockProperties.getAllBlocks()) {
            VisImageButton button = new VisImageButton(new TextureRegionDrawable(Game.assets.get(properties.getFullTexturePath(), Texture.class)),
                    properties.name);
            buildSelectionButtons.add(button);
            buildSelectionContainer.addActor(button);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    screen.getWorld().getPlayer().setBlockSelected(properties.id);
                }
            });
        }

        buildSelectionButtons.getButtons().get(0).setChecked(true);
        buildWindow.add(buildSelectionContainer).size(300, 150);
        buildWindow.pack();
        buildWindow.setPosition(VIEWPORT_WIDTH, 0, Align.bottomRight);
        addActor(buildWindow);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
        staminaBar.setValue(screen.getWorld().getPlayer().getStamina());
    }
}
