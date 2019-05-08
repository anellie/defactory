package xyz.angm.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.kotcrab.vis.ui.widget.VisProgressBar;

import static xyz.angm.game.world.entities.Player.PLAYER_HEALTH;

/** The player HUD containing all GUI elements of the player, like health bar or inventory. */
class PlayerHud extends Group {

    private final GameScreen screen;
    private final VisProgressBar healthBar = new VisProgressBar(0, PLAYER_HEALTH, 1, false);

    /** Construct a new HUD.
     * @param screen The screen the HUD will be a part of */
    PlayerHud(GameScreen screen) {
        this.screen = screen;
        setPosition(0, 0);

        addActor(healthBar);
        healthBar.setSize(600, 15);
        healthBar.setPosition(0, 0);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
    }
}
