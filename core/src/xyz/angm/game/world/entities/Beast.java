package xyz.angm.game.world.entities;

import xyz.angm.game.world.TileVector;

/** Represents an enemy the player is trying to defend against.
 * The goal of beasts is to destroy the player's base, attacking in waves. */
public class Beast extends Entity {

    /** The maximum beast health. */
    private static final int BEAST_HEALTH = 20;

    /** Required for kryo deserialization; needs a no-arg constructor. */
    private Beast() {
        super(1);
        health = BEAST_HEALTH;
        actorTexture = "textures/beast.png";
    }

    /** Constructs a beast. Requires AssetManager in Game to be ready.
     * @param position The position of the beast. */
    public Beast(TileVector position) {
        this();
        getPosition().set(position.getX(), position.getY());
    }
}