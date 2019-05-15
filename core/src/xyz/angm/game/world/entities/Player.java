package xyz.angm.game.world.entities;

import xyz.angm.game.world.Block;

import java.util.Arrays;

import static xyz.angm.game.world.TerrainGenerator.WORLD_SIZE_MULTIPLICATOR;
import static xyz.angm.game.world.World.WORLD_VIEWPORT_HEIGHT;
import static xyz.angm.game.world.World.WORLD_VIEWPORT_WIDTH;

/** The player in the game. Their goal is building a base to defend against beasts.*/
public class Player extends Entity {

    /** The maximum player health. */
    public static final int PLAYER_HEALTH = 20;
    /** The maximum stamina. Sprinting depletes stamina; not sprinting refills */
    public static final float PLAYER_STAMINA = 7.5f;
    private static final float SPRINT_MULTIPLIER = 1.5f;

    private float stamina = PLAYER_STAMINA;
    private boolean isSprinting = false;
    private int blockSelected = 0;
    private Block.Direction blockDirection = Block.Direction.UP;

    /** Constructs a Player. Requires AssetManager in Game to be ready. */
    public Player() {
        super(1);
        health = PLAYER_HEALTH;
        actorTexture = "textures/player.png";
        getPosition().set((WORLD_SIZE_MULTIPLICATOR / 2f) * WORLD_VIEWPORT_WIDTH, (WORLD_SIZE_MULTIPLICATOR / 2f) * WORLD_VIEWPORT_HEIGHT);
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

    public Block.Direction getBlockDirection() {
        return blockDirection;
    }

    /** Cycles to the next block direction. */
    public void cycleDirection() {
        int newIndex = Arrays.asList(Block.Direction.values()).indexOf(blockDirection) + 1;
        if (newIndex == Block.Direction.values().length) newIndex = 0;
        blockDirection = Block.Direction.values()[newIndex];
    }
}
