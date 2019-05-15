package xyz.angm.game.world;

import com.badlogic.gdx.graphics.Texture;
import xyz.angm.game.Game;

/** A material. Used by the player to construct blocks.
 * Some blocks produce materials, others require them to function. */
@SuppressWarnings("JavaDoc")
public enum Material {

    WOOD,
    STONE,
    IRON,
    DIAMOND;

    /** Get the texture.
     * @return The texture of this material. */
    public Texture getTexture() {
        return Game.assets.get("textures/materials/" + this.name().toLowerCase() + ".png", Texture.class);
    }

    /** Load/Register all textures into the game asset manager. */
    public static void loadTextures() {
        for (Material material : values()) {
            Game.assets.load("textures/materials/" + material.name().toLowerCase() + ".png", Texture.class);
        }
    }
}
