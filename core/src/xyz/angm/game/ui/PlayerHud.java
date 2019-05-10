package xyz.angm.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisWindow;

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
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
        staminaBar.setValue(screen.getWorld().getPlayer().getStamina());
    }
}
