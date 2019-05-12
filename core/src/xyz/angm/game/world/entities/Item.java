package xyz.angm.game.world.entities;

import xyz.angm.game.world.TileVector;

/** An item entity. Created when a machine creating materials is connected with conveyor belts. */
public class Item extends Entity {

    public Item(TileVector position) {
        super(1);
        this.getPosition().set(position.getX(), position.getY());
    }
}
