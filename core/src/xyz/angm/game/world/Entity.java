package xyz.angm.game.world;

import xyz.angm.game.util.IntVector2;

/** An entity is a component capable of changing its position and interacting with the world. */
abstract class Entity {

    /** The position of the entity. */
    IntVector2 position = new IntVector2();
    /** The speed the entity is travelling at. */
    IntVector2 velocity = new IntVector2();
    /** The entities health. 0 will cause the entity to be disposed. */
    int health;
}
