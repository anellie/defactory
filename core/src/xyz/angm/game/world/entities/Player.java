package xyz.angm.game.world.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import xyz.angm.game.Game;

import static xyz.angm.game.ui.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.Screen.VIEWPORT_WIDTH;
import static xyz.angm.game.world.TerrainGenerator.WORLD_SIZE_MULTIPLICATOR;

/** The player in the game. Their goal is building a base to defend against beasts.*/
public class Player extends Entity {

    /** The maximum player health. */
    public static final int PLAYER_HEALTH = 20;
    /** The maximum stamina. Sprinting depletes stamina; not sprinting refills */
    public static final float PLAYER_STAMINA = 5f;
    private static final float SPRINT_MULTIPLIER = 2.5f;

    private float stamina = PLAYER_STAMINA;
    private boolean isSprinting = false;
    private int blockSelected = 0;

    /** Constructs a Player. Requires AssetManager in Game to be ready. */
    public Player() {
        health = PLAYER_HEALTH;
        actor = new Image(Game.assets.get("textures/player.png", Texture.class));
        getPosition().set((WORLD_SIZE_MULTIPLICATOR / 2f) * VIEWPORT_WIDTH, (WORLD_SIZE_MULTIPLICATOR / 2f) * VIEWPORT_HEIGHT);
    }

    /** Toggles the player sprinting. Sprinting causes the player to move faster.
     * @param sprint (false: Stop sprinting) (true: start sprinting) */
    public void sprint(boolean sprint) {
        if (isSprinting == sprint) return;
        isSprinting = sprint;

        if (sprint) movementMultiplier *= SPRINT_MULTIPLIER;
        else movementMultiplier /= SPRINT_MULTIPLIER;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isSprinting) stamina -= delta;
        else stamina += delta;

        if (stamina < 0) sprint(false);
        stamina = Math.max(0, Math.min(PLAYER_STAMINA, stamina));
    }

    public float getStamina() {
        return stamina;
    }

    public int getBlockSelected() {
        return blockSelected;
    }

    public void setBlockSelected(int blockSelected) {
        this.blockSelected = blockSelected;
    }
}
