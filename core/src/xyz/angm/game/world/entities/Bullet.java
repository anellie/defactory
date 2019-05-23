package xyz.angm.game.world.entities;

import xyz.angm.game.world.TileVector;

/** A bullet entity. Created when a shoots at a beast. */
public class Bullet extends Entity {

    /** Create a new bullet.
     * @param position The position in world coordinates; actual position is not tile-restricted. */
    public Bullet(TileVector position) {
        super(0.1f);
        getPosition().set(position.getX(), position.getY());
        actorTexture = "textures/bullet.png";
    }

    /** Set rotation of the bullet.
     * @param rotation The new rotation. */
    public void setRotation(float rotation) {
        actor.setRotation(rotation);
    }
}
