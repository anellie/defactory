package xyz.angm.game.world;

/** A runnable used to allow all blocks to update themselves. Run from World. */
class BlockTickRunner implements Runnable {

    private final World world;
    private final TileVector tmpTV = new TileVector();

    /** Create a new runnable.
     * @param world The world. */
    BlockTickRunner(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        world.map.iterateBlocks(this::tickBlock);
    }

    private void tickBlock(Block block) {
        if (block.getProperties().materialProduced != null) { // Block produces material
            // Could NOT come up with an elegant way to check all surrunding blocks... TODO maybe?
            Block tmpB = world.map.getBlock(tmpTV.set(block.getPosition()).add(1, 0));
            if (!isConveyor(tmpB)) tmpB = world.map.getBlock(tmpTV.set(block.getPosition()).add(-1, 0));
            if (!isConveyor(tmpB)) tmpB = world.map.getBlock(tmpTV.set(block.getPosition()).add(0, 1));
            if (!isConveyor(tmpB)) tmpB = world.map.getBlock(tmpTV.set(block.getPosition()).add(0, -1));

            // No conveyor around, put it into the player inventory
            if (!isConveyor(tmpB)) world.getPlayer().inventory.add(block.getProperties().materialProduced, 1);
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
    }

    private static boolean isConveyor(Block block) {
        return block != null && block.getProperties().type == BlockType.CONVEYOR;
    }
}
