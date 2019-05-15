package xyz.angm.game.world.entities;

import xyz.angm.game.world.TileVector;
import xyz.angm.game.world.blocks.Material;

/** An item entity. Created when a machine creating materials is connected with conveyor belts. */
public class Item extends Entity {

    private final Material material;

    /** Create a new item entity.
     * @param material The material of the item.
     * @param position The position in world coordinates; actual position is not tile-restricted. */
    public Item(TileVector position, Material material) {
        super(0.5f);
        this.material = material;
        getPosition().set(position.getX(), position.getY());
        actorTexture = "textures/materials/" + material.name().toLowerCase() + ".png";
    }
}
