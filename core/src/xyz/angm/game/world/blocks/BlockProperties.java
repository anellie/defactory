package xyz.angm.game.world.blocks;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

/** A class holding all (static) properties of a block.
 * All block types (and their properties) are loaded from a JSON document on boot.
 * All properties are initialised to their defaults; this saves disk space since they do not need to be in the JSON document.
 *
 * These objects should NOT be directly modified. Them being non-final is due to them needing to be modified during deserialization.
 * Using getters for all of these values to prevent modification would be possible;
 * but 100+ lines of boilerplate code isn't that great or practical, so the security risk of mutable fields is preferred. */
@SuppressWarnings({"CanBeFinal", "WeakerAccess"}) // Can't be final or private due to serializer
public class BlockProperties {

    /** The ID of a block type used to determine equality. Should not be the same on different block properties. */
    public int id = -1;
    /** Health of a block; eg how much enemy hits it can take. */
    public int health = 500;
    /** The type of the block. See BlockType enum. */
    public BlockType type = BlockType.DEFAULT;
    /** Set the block to be a physics sensor (meaning it cannot collide with other entities). */
    public boolean isSensor = false;

    /** If this block is in the build menu. */
    public boolean displayedInBuildMenu = true;
    /** The path to the blocks texture relative to '@/core/assets/textures'. Does not include '.png' suffix. */
    public String texture = "";
    /** Name of a blocks localization string. To get a blocks localized string: Localization.get("block" + name) */
    public String name = "Unknown";
    /** The category the block will be placed in. */
    public String category = "General";

    /** The material this block produces. Will be output to conveyor belts or put in the players inventory if none present. */
    public Material materialProduced = null;
    /** The material this block requires to work/do its task. */
    public Material materialRequired = null;

    /** TURRET & HEALER specific: The range of the block. */
    public float range = -1;

    /** TURRET specific: The amount of beasts the turret can hit at once. */
    public int turretFireRate = 1;
    /** TURRET specific: The damage of the turret per shot. */
    public int turretDamage = 1;

    /** HEALER specific: The damage recovered per heal cycle per block. */
    public int healerRecovery = 1;

    public String getFullTexturePath() {
        return "textures/blocks/" + texture + ".png";
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof BlockProperties) && id == ((BlockProperties) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }


    private static List<BlockProperties> allBlockTypes;

    /** Returns all block properties known. The list returned is immutable. */
    public static List<BlockProperties> getAllBlocks() {
        if (allBlockTypes == null) loadBlocks();
        return allBlockTypes;
    }

    /** Returns block properties.
     * @param id The ID of the properties
     * @return The properties with matching ID */
    public static BlockProperties getProperties(int id) {
        if (allBlockTypes == null) loadBlocks();
        return allBlockTypes.stream().filter(properties -> properties.id == id).findFirst().orElse(null);
    }

    // Load all blocks. Automatically done on first access to getProperties.
    private static void loadBlocks() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            allBlockTypes = Collections.unmodifiableList(
                    mapper.readValue(
                            Gdx.files.internal("data/blocks.json").readString(),
                            new TypeReference<List<BlockProperties>>() {}
                    )
            );
        } catch (Exception e) {
            Gdx.app.error("World", "Fatal error while loading blocks. Exiting.", e);
            Gdx.app.exit();
        }
    }
}