package xyz.angm.game.world.blocks;

/** The type of a block determines its capabilities and behavior. */
public enum BlockType {
    /** A block with no special properties. */
    DEFAULT,
    /** A block capable of moving entities on top of it. */
    CONVEYOR,
    /** A block emitting light. */
    TORCH,
    /** A block capable of damaging enemies over a given range by shooting bullets. */
    TURRET,
    /** A block capable of healing other blocks in a given range. */
    HEALER,
    /** A block vital to the game. If this block is destroyed, the player loses. */
    CORE
}