package xyz.angm.game.world.blocks;

import xyz.angm.game.world.TileVector;
import xyz.angm.game.world.World;

/** A runnable used to allow all blocks to update themselves. Is run from World. */
public class BlockTickRunner implements Runnable {

    private final World world;
    private final TileVector tmpTV = new TileVector();
    private final Block[] tmpBlockArray = new Block[4];

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
            boolean conveyorFound = false;
            tmpBlockArray[0] = world.map.getBlock(tmpTV.set(block.getPosition()).add(0, -1));
            tmpBlockArray[1] = world.map.getBlock(tmpTV.set(block.getPosition()).add(-1, 0));
            tmpBlockArray[2] = world.map.getBlock(tmpTV.set(block.getPosition()).add(0, 1));
            tmpBlockArray[3] = world.map.getBlock(tmpTV.set(block.getPosition()).add(1, 0));

            for (int i = 0; i < tmpBlockArray.length; i++) {
                if (isConveyor(tmpBlockArray[i], Block.Direction.values()[i])) {
                    world.spawnItem(tmpBlockArray[i].getPosition(), block.getProperties().materialProduced);
                    conveyorFound = true;
                    break;
                }
            }

            // No conveyor around, put it into the player inventory
            if (!conveyorFound) world.getPlayer().inventory.add(block.getProperties().materialProduced, 1);
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

    /** Is the block NOT a conveyor able to receive items? */
    private static boolean isConveyor(Block block, Block.Direction direction) {
        return block != null && block.getProperties().type == BlockType.CONVEYOR && block.getDirection() != direction;
    }
}
