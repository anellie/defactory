package xyz.angm.game.world;

import xyz.angm.game.util.IntVector2;

abstract class Entity {

    IntVector2 position = new IntVector2();
    IntVector2 velocity = new IntVector2();
    int health;
}
