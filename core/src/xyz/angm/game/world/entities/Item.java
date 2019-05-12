package xyz.angm.game.world.entities;

import xyz.angm.game.world.TileVector;

/** An item entity. Created when a machine creating materials is connected with conveyor belts. */
public class Item extends Entity {

    private final ItemType type;

    /** Create a new item entity.
     * @param type The type of the item.
     * @param position The position in world coordinates; actual position is not tile-restricted. */
    public Item(TileVector position, ItemType type) {
        super(1);
        this.type = type;
        getPosition().set(position.getX(), position.getY());
        actorTexture = "textures/items/" + type.name().toLowerCase() + ".png";
    }

    /** All available item types. */
    @SuppressWarnings("JavaDoc")
    public enum ItemType {
        STONE, IRON, GOLD, DIAMOND
    }
}
