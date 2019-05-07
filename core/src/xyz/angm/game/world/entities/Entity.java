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
    /** The entities health. 0 will cause the entity to be disposed. */
    int health;
    /** Actor for displaying on the screen. */
    Image actor;

    public IntVector2 getPosition() {
        return position;
    }

    public IntVector2 getVelocity() {
        return velocity;
    }

    /** Adds itself to the given stage.
     * @param stage Stage to be added to */
    public void registerToStage(Stage stage) {
        stage.addActor(actor);
        actor.setPosition(position.x, position.y);
    }
}
