package xyz.angm.game.world.entities;

import com.badlogic.gdx.math.Vector2;
import xyz.angm.game.world.TileVector;

/** A bullet entity. Created when a shoots at a beast. */
public class Bullet extends Entity {

    /** Create a new bullet.
     * @param position The position in world coordinates; actual position is not tile-restricted.
     * @param target The position of the target of the bullet. */
    public Bullet(TileVector position, Vector2 target) {
        super(0.1f);
        getPosition().set(position.getX(), position.getY()).add(0.5f, 0.5f);
        getVelocity().set(target).sub(position.getX(), position.getY()).limit(1f);
        // Prevent the bullet from getting stuck in the block it's spawning from
        getPosition().add(getVelocity());
        actorTexture = "textures/bullet.png";
    }

    /** Set rotation of the bullet.
     * @param rotation The new rotation. */
    public void setRotation(float rotation) {
        actor.setRotation(rotation);
    }
}
