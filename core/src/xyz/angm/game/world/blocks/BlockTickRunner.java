package xyz.angm.game.world.blocks;

import xyz.angm.game.world.TileVector;
import xyz.angm.game.world.World;

/** A runnable used to allow all blocks to update themselves. Is run from World. */
public class BlockTickRunner implements Runnable {

    private final World world;
    private final TileVector tmpTV = new TileVector();

    /** Create a new runnable.
     * @param world The world. */
    public BlockTickRunner(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        world.map.iterateBlocks(this::tickBlock);
    }

    private void tickBlock(Block block) {
        if (!block.canWork()) return;

        if (block.getProperties().materialProduced != null) { // Block produces material
            // Search a conveyor next to this block
            Block blockNextTo = world.map.getBlock(tmpTV.set(block.getPosition()).add(1, 0));
            if (isNotConveyor(blockNextTo)) blockNextTo = world.map.getBlock(tmpTV.set(block.getPosition()).add(-1, 0));
            if (isNotConveyor(blockNextTo)) blockNextTo = world.map.getBlock(tmpTV.set(block.getPosition()).add(0, 1));
            if (isNotConveyor(blockNextTo)) blockNextTo = world.map.getBlock(tmpTV.set(block.getPosition()).add(0, -1));

            // No conveyor around, put it into the player inventory
            if (isNotConveyor(blockNextTo)) world.getPlayer().inventory.add(block.getProperties().materialProduced, 1);
            // Conveyor around, put it onto the conveyor
            else world.spawnItem(tmpTV, block.getProperties().materialProduced);
        }

        // Run type-specific actions
        switch (block.getProperties().type) {
            case TURRET:
                // TODO
                break;
            case HEALER:
                // TODO
                break;
            default:
                break;
        }

        block.decrementMaterial();
    }

    /** Is the block NOT a conveyor? */
    private static boolean isNotConveyor(Block block) {
        return block == null || block.getProperties().type != BlockType.CONVEYOR;
    }
}
