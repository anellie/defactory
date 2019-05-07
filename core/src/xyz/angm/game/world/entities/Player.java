package xyz.angm.game.world.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import xyz.angm.game.Game;

/** The player in the game. Their goal is building a base to defend against beasts.*/
public class Player extends Entity {

    private static final int PLAYER_HEALTH = 20;

    /** Constructs a Player. Requires AssetManager in Game to be ready. */
    public Player() {
        health = PLAYER_HEALTH;
        actor = new Image(Game.assets.get("./textures/player.png", Texture.class));
    }
}
