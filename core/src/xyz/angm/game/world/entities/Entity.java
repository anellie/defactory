package xyz.angm.game.world.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.io.Serializable;

/** An entity is a component capable of changing its position and interacting with the world. */
abstract class Entity implements Serializable {

    private static final float MOVEMENT_MULTIPLIER = 100f;

    /** The position of the entity. */
    private final Vector2 position = new Vector2();
    /** The speed the entity is travelling at. */
    private final Vector2 velocity = new Vector2();
    /** A vector used for calculation. It's a class member to prevent creating one-time-use vectors. */
    private final transient Vector2 tmpV = new Vector2();
    /** The entities health. 0 will cause the entity to be disposed. */
    int health;
    /** Actor for displaying on the screen. */
    transient Image actor;

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    /** Should be called every frame on the server so the entity can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        // Update position by velocity. Time between calls is used to prevent FPS from affecting entity speed
        position.add(tmpV.set(velocity).scl(delta * MOVEMENT_MULTIPLIER));
        actor.setPosition(position.x, position.y);
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    public void registerToStage(Stage stage) {
        stage.addActor(actor);
        actor.setPosition(position.x, position.y);
    }
}
