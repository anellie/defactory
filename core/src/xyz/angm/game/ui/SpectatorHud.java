package xyz.angm.game.ui;

import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.angm.game.ui.screens.GameScreen;

import static xyz.angm.game.world.entities.Player.PLAYER_HEALTH;

/** The spectator HUD containing all GUI elements of a spectator. */
public class SpectatorHud extends PlayerHud {

    private final GameScreen screen;
    private final VisProgressBar healthBar = new VisProgressBar(0, PLAYER_HEALTH, 1, false, "health-bar");

    /** Construct a new HUD.
     * @param screen The screen the HUD will be a part of */
    public SpectatorHud(GameScreen screen) {
        super(screen);
        this.screen = screen;
        reload();
    }

    @Override
    public void reload() {
        clear();

        // Window containing health + stamina bar
        VisWindow barWindow = new VisWindow(Localization.get("hudStatus"));
        barWindow.add(healthBar).size(BAR_WIDTH, BAR_HEIGHT).row();
        barWindow.pack();
        addActor(barWindow);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
    }
}
