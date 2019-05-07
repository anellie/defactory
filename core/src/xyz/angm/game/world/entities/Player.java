package xyz.angm.game.world.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import xyz.angm.game.Game;

import static xyz.angm.game.ui.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.Screen.VIEWPORT_WIDTH;
import static xyz.angm.game.world.TerrainGenerator.WORLD_SIZE_MULTIPLICATOR;

/** The player in the game. Their goal is building a base to defend against beasts.*/
public class Player extends Entity {

    private static final int PLAYER_HEALTH = 20;
    private static final float SPRINT_MULTIPLIER = 2.5f;

    /** Constructs a Player. Requires AssetManager in Game to be ready. */
    public Player() {
        health = PLAYER_HEALTH;
        actor = new Image(Game.assets.get("./textures/player.png", Texture.class));
        getPosition().set((WORLD_SIZE_MULTIPLICATOR / 2f) * VIEWPORT_WIDTH, (WORLD_SIZE_MULTIPLICATOR / 2f) * VIEWPORT_HEIGHT);
    }

    /** Toggles the player sprinting. Sprinting causes the player to move faster.
     * @param sprint (false: Stop sprinting) (true: start sprinting) */
    public void sprint(boolean sprint) {
        if (sprint) movementMultiplier *= SPRINT_MULTIPLIER;
        else movementMultiplier /= SPRINT_MULTIPLIER;
    }
}
