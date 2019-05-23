package xyz.angm.game.world.blocks;

import com.badlogic.gdx.math.Circle;
import xyz.angm.game.world.TileVector;
import xyz.angm.game.world.World;
import xyz.angm.game.world.entities.Beast;

/** A runnable used to allow all blocks to update themselves. Is run from World. */
public class BlockTickRunner implements Runnable {

    private final World world;
    private final TileVector tmpTV = new TileVector();
    private final TileVector[] tmpBlockArray = new TileVector[4];
    private final Circle rangeCircle = new Circle();

    /** Create a new runnable.
     * @param world The world. */
    public BlockTickRunner(World world) {
        this.world = world;
        tmpBlockArray[0] = new TileVector().set(0, -1);
        tmpBlockArray[1] = new TileVector().set(-1, 0);
        tmpBlockArray[2] = new TileVector().set(0, 1);
        tmpBlockArray[3] = new TileVector().set(1, 0);
    }

    @Override
    public void run() {
        world.map.iterateBlocks(this::tickBlock);
    }

    private void tickBlock(Block block) {
        if (!block.canWork()) return;
        BlockProperties props = block.getProperties();
        TileVector position = block.getPosition();

        if (props.materialProduced != null) processMaterialProduced(block);

        if (props.range > -1) {
            rangeCircle.set(position.getX(), position.getY(), props.range);

            // Run type-specific actions
            if (props.type == BlockType.TURRET) processTurret(props);
            else if (props.type == BlockType.HEALER) processHealer(props);
        }

        block.decrementMaterial();
    }

    // Called on blocks where material produced != null
    private void processMaterialProduced(Block block) {
        for (int i = 0; i < tmpBlockArray.length; i++) {
            tmpTV.set(tmpBlockArray[i]).add(block.getPosition());
            if (isConveyor(world.map.getBlock(tmpTV), Block.Direction.values()[i])) {
                world.spawnItem(tmpTV, block.getProperties().materialProduced);
                return;
            }
        }

        // No conveyor around since loop didn't return, put it into the player inventory
        world.getPlayer().inventory.add(block.getProperties().materialProduced, 1);
    }

    // Is the block a conveyor able to receive items?
    private static boolean isConveyor(Block block, Block.Direction direction) {
        return block != null && block.getProperties().type == BlockType.CONVEYOR && block.getDirection() != direction;
    }

    // Process turret. rangeCircle should be set to the block already.
    private void processTurret(BlockProperties props) {
        int shotsLeft = props.turretFireRate;
        for (Beast beast : world.getBeasts()) {
            if (rangeCircle.contains(beast.getPosition())) {
                beast.removeHealth(props.turretDamage);
                shotsLeft--;
                if (beast.getHealth() < 0) world.removeBeast(beast);
                if (shotsLeft <= 0) return;
            }
        }
    }

    // Process healer. rangeCircle should be set to the block already.
    private void processHealer(BlockProperties props) {
        world.map.iterateBlocks(otherBlock -> {
            if (rangeCircle.contains(otherBlock.getPosition().getX() + 0.5f, otherBlock.getPosition().getY() + 0.5f)) {
                otherBlock.addToHealth(props.healerRecovery);
            }
        });
    }
}
