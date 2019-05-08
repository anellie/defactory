package xyz.angm.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.kotcrab.vis.ui.widget.VisProgressBar;

import static xyz.angm.game.world.entities.Player.PLAYER_HEALTH;
import static xyz.angm.game.world.entities.Player.PLAYER_STAMINA;

/** The player HUD containing all GUI elements of the player, like health bar or inventory. */
class PlayerHud extends Group {

    private static final int BAR_WIDTH = 600;
    private static final int BAR_HEIGHT = 15;

    private final GameScreen screen;
    private final VisProgressBar healthBar = new VisProgressBar(0, PLAYER_HEALTH, 1, false);
    private final VisProgressBar staminaBar = new VisProgressBar(0, PLAYER_STAMINA, PLAYER_STAMINA / BAR_WIDTH, false);

    /** Construct a new HUD.
     * @param screen The screen the HUD will be a part of */
    PlayerHud(GameScreen screen) {
        this.screen = screen;
        setPosition(0, 0);

        addActor(healthBar);
        healthBar.setSize(BAR_WIDTH, BAR_HEIGHT);
        healthBar.setPosition(0, 0);

        addActor(staminaBar);
        staminaBar.setSize(BAR_WIDTH, BAR_HEIGHT);
        staminaBar.setPosition(0, BAR_HEIGHT);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
        staminaBar.setValue(screen.getWorld().getPlayer().getStamina());
    }
}
