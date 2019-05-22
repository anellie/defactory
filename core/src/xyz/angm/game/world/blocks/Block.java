package xyz.angm.game.world.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;
import xyz.angm.game.Game;
import xyz.angm.game.world.TileVector;

/** A block can be placed by the player, onto a tile in the world map.
 * Different block types have different function. */
public class Block implements Disposable {

    private int type;
    private final TileVector position = new TileVector();
    private Direction direction;
    private int health;
    private transient Image actor;
    private transient HealthBar healthBar;
    /** The amount of the material the block currently contains, should it need any material to run. */
    private int materialRequiredAmount = 0;

    /** Required for kryo deserialization; needs a no-arg constructor. */
    private Block() {}

    /** Construct a new block at the specified position. Call registerToStage to display it.
     * @param position The position of the block. Actor position is also set with this.
     * @param type The type of this block.
     * @param direction The direction the block is facing. */
    public Block(TileVector position, int type, Direction direction) {
        this();
        this.position.set(position);
        this.type = type;
        this.direction = direction;
        this.health = getProperties().health;
    }

    public TileVector getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getHealth() {
        return health;
    }

    public BlockProperties getProperties() {
        return BlockProperties.getProperties(type);
    }

    /** Adds itself to the given group.
     * @param group Group to be added to */
    public void registerToGroup(Group group) {
        if (actor == null) {
            actor = new Image(Game.assets.get(getProperties().getFullTexturePath(), Texture.class));
            healthBar = new HealthBar();
        }
        group.addActor(actor);
        actor.setSize(1, 1);
        actor.setOrigin(Align.center);
        actor.setPosition(position.getX(), position.getY());
        actor.setRotation(direction.toDegrees());
        group.addActor(healthBar);
        healthBar.setPosition(position.getX(), position.getY());
    }

    /** Can this block do work? Work is anything done by BlockTickRunner.
     * @return If the block can work. */
    boolean canWork() {
        return getProperties().materialRequired == null || materialRequiredAmount > 0;
    }

    /** Call when the amount of material in the bloc should be incremented. */
    public void incrementMaterial() {
        materialRequiredAmount++;
    }

    /** Call when the amount of material in the bloc should be decremented. Usually when the block does work. */
    void decrementMaterial() {
        materialRequiredAmount--;
    }

    /** Call when the block was hit by a beast. */
    public void onHit() {
        health--;
    }

    /** Adds to the HP, 'healing' the block.
     * @param amount The amount to heal. */
    void addToHealth(int amount) {
        health += amount;
        health = Math.min(health, getProperties().health); // Cap it
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
            healthBar.remove();
        }
    }

    /** The direction a block can be facing. Needed by some blocks; eg conveyor belts. */
    @SuppressWarnings("JavaDoc")
    public enum Direction {
        UP, RIGHT, DOWN, LEFT;

        /** Convert this direction to degrees.
         * @return The direction in degrees; right = 0deg; counter-clockwise (same as LibGDX actor rotation). */
        public int toDegrees() {
            if (this == Direction.UP) return 90;
            else if (this == Direction.RIGHT) return 0;
            else if (this == Direction.DOWN) return 270;
            else return 180;
        }
    }

    /** A simple health bar displaying at the bottom of a block. LibGDX ProgressBar did not want to work. */
    private class HealthBar extends Actor {

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (health == getProperties().health) return; // Bar is full, don't show
            VisUI.getSkin().getDrawable("black-transparent").draw(batch, getX(), getY(), 1f, 0.1f);
            VisUI.getSkin().getDrawable("green").draw(batch, getX(), getY(), (float) health / getProperties().health, 0.1f);
        }
    }
}
