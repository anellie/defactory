package xyz.angm.game.world.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import xyz.angm.game.Game;

import java.io.Serializable;

/** An entity is a component capable of changing its position and interacting with the world. */
public abstract class Entity implements Serializable {

    /** The position of the entity. */
    private final Vector2 position = new Vector2();
    /** The speed the entity is travelling at. */
    private final Vector2 velocity = new Vector2();
    /** The multiplier used to apply velocity. */
    float movementMultiplier = 5f;
    /** The entities health. 0 will cause the entity to be disposed. */
    int health;
    /** The size of the actor. */
    public final float entitySize;
    /** Actor for displaying on the screen. */
    private transient Image actor;
    /** Location of the actor's texture. */
    transient String actorTexture;

    /** Create a new entity.
     * @param entitySize The size of the entity in meters. (eg 1 block = 1 meter) */
    Entity(float entitySize) {
        this.entitySize = entitySize;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getMovementMultiplier() {
        return movementMultiplier;
    }

    /** Should be called every frame on the server so the entity can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        actor.setPosition(position.x, position.y);
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    public void registerToStage(Stage stage) {
        if (actor == null) actor = new Image(Game.assets.get(actorTexture, Texture.class));
        stage.addActor(actor);
        actor.setPosition(position.x, position.y);
        actor.setSize(entitySize, entitySize);
    }

    public int getHealth() {
        return health;
    }
}
