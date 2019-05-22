package xyz.angm.game.ui;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisWindow;
import xyz.angm.game.ui.screens.GameScreen;

import static xyz.angm.game.ui.screens.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.screens.Screen.VIEWPORT_WIDTH;
import static xyz.angm.game.world.entities.Player.PLAYER_HEALTH;

/** The spectator HUD containing all GUI elements of a spectator.
 * Extends player hud; overrides reload to set own elements instead. */
public class SpectatorHud extends PlayerHud {

    private final GameScreen screen;
    private final VisProgressBar healthBar = new VisProgressBar(0, PLAYER_HEALTH, 1, false, "health-bar");
    private VisWindow beastsLeftWindow;

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

        // Window containing health bar of the player
        VisWindow barWindow = new VisWindow(Localization.get("hudStatus"));
        barWindow.add(healthBar).size(BAR_WIDTH, BAR_HEIGHT).row();
        barWindow.pack();
        addActor(barWindow);

        // Window containing the amount of beasts the spectator can spawn
        beastsLeftWindow = new VisWindow(Localization.get("hudBeastsLeft", 0));
        beastsLeftWindow.setPosition(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, Align.topRight);
        beastsLeftWindow.pack();
        addActor(beastsLeftWindow);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setValue(screen.getWorld().getPlayer().getHealth());
        beastsLeftWindow.setName(Localization.get("hudBeastsLeft", screen.getWorld().getBeastsLeft()));
    }
}
