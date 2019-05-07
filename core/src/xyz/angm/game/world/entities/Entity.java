package xyz.angm.game.world.entities;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import xyz.angm.game.util.IntVector2;

import java.io.Serializable;

/** An entity is a component capable of changing its position and interacting with the world. */
abstract class Entity implements Serializable {

    /** The position of the entity. */
    private final IntVector2 position = new IntVector2();
    /** The speed the entity is travelling at. */
    private final IntVector2 velocity = new IntVector2();
    /** A vector used for calculation. It's a class member to prevent creating one-time-use vectors. */
    private final transient IntVector2 tmpIV = new IntVector2();
    /** The entities health. 0 will cause the entity to be disposed. */
    int health;
    /** Actor for displaying on the screen. */
    transient Image actor;

    public IntVector2 getPosition() {
        return position;
    }

    public IntVector2 getVelocity() {
        return velocity;
    }

    /** Should be called every frame on the server so the entity can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        // Update position by velocity. Time between calls is used to prevent FPS from affecting entity speed
        position.add(tmpIV.set(velocity).multiply(delta));
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    public void registerToStage(Stage stage) {
        stage.addActor(actor);
        actor.setPosition(position.x, position.y);
    }
}
