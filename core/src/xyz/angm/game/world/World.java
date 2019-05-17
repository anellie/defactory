package xyz.angm.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import xyz.angm.game.Game;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.blocks.BlockProperties;
import xyz.angm.game.world.blocks.BlockTickRunner;
import xyz.angm.game.world.blocks.Material;
import xyz.angm.game.world.entities.Beast;
import xyz.angm.game.world.entities.Item;
import xyz.angm.game.world.entities.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static xyz.angm.game.world.TerrainGenerator.WORLD_SIZE_MULTIPLICATOR;

/** Represents the game world and contains all entities and the world map. */
public class World implements Disposable {

    /** The width of the world viewport. Should be in meters due to the physics system. */
    public static final float WORLD_VIEWPORT_WIDTH = 120f;
    /** The height of the world viewport. Should be in meters due to the physics system. */
    public static final float WORLD_VIEWPORT_HEIGHT = 67.5f;
    /** The frequency at which blocks update; ie they execute their action. Unit is milliseconds. */
    private static final long BLOCK_TICK_FREQ = 1000;

    /** Seed used for generating terrain. See {@link TerrainGenerator}. */
    public final long seed;
    /** Map containing the map. (Thanks, Sherlock.) */
    public final WorldMap map;
    private final Player player = new Player();
    private final Array<Item> items = new Array<>(false, 16);
    private final Array<Beast> beasts = new Array<>(true, 16);
    private final Array<Vector2> beastPositions = new Array<>(true, 16);
    private final PhysicsEngine physics;

    private final Stage stage = new Stage(new FitViewport(WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_HEIGHT));
    private Vector2 cameraPosition = player.getPosition();
    private final Image selector = new Image();
    private final TileVector selectorPosition = new TileVector();
    private final Group blockGroup = new Group();

    private final Vector2 tmpV = new Vector2();

    /** Constructs a new world along with it's map.
     * @param generator The generator which is done loading.
     * @param active If false, background activity is heavily restricted. Used on the client. */
    public World(TerrainGenerator generator, boolean active) {
        this.seed = generator.seed;
        map = new WorldMap(generator);
        physics = new PhysicsEngine(player, active);

        if (active)
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(new BlockTickRunner(this), BLOCK_TICK_FREQ, BLOCK_TICK_FREQ, TimeUnit.MILLISECONDS);

        stage.addActor(blockGroup);
        player.registerToStage(stage);
        stage.addActor(selector);

        selector.setSize(1, 1);
        selector.setColor(1, 1, 1, 0.5f);
        selector.setOrigin(Align.center);
        ((OrthographicCamera) stage.getCamera()).zoom = 0.2f;
    }

    /** Returns the player entity.
     * @return The player */
    public Player getPlayer() {
        return player;
    }

    /** Should be called every frame on the server so the world can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        physics.act(delta);
        player.act(delta);
        stage.act(delta);
        items.forEach(item -> item.act(delta));
        beasts.forEach(beast -> beast.act(delta));
    }

    /** Should be called every frame when the world should render itself and all components. */
    public void render() {
        updateCamera();

        // Draw map first for lighting effects
        stage.getBatch().begin();
        map.draw(stage.getBatch(), 1f);
        stage.getBatch().end();

        physics.render((OrthographicCamera) stage.getCamera());
        stage.draw();
    }

    /** Causes the camera to be independent on the player's position. Call on client. */
    public void freeCamera() {
        cameraPosition = cameraPosition.cpy();
    }

    /** Moves the camera. Requires freeCamera to be called; will throw exception otherwise.
     * @param v The vector added to the camera position. */
    public void moveCamera(Vector2 v) {
        if (player.getPosition() == cameraPosition) throw new UnsupportedOperationException("Camera needs to be freed first!");
        cameraPosition.add(v);
    }

    /** Updates the block selector position.
     * @param x The X axis of the screen coordinates
     * @param y The Y axis of the screen coordinates */
    public void updateSelector(int x, int y) {
        tmpV.set(x, y);
        stage.screenToStageCoordinates(tmpV);
        selectorPosition.set(tmpV);
        selector.setPosition(selectorPosition.getX(), selectorPosition.getY());
        selector.setDrawable(new TextureRegionDrawable(new TextureRegion(Game.assets.get(BlockProperties.getProperties(getPlayer().getBlockSelected()).getFullTexturePath(), Texture.class))));
        selector.setRotation(player.getBlockDirection().toDegrees());
    }

    /** Zooms the world map; scaling it bigger or smaller.
     * @param zoom The zoom amount. */
    public void zoomMap(float zoom) {
        // Limit the zoom from 0 to world size to prevent unwanted behavior
        ((OrthographicCamera) stage.getCamera()).zoom =
                Math.max(0, Math.min(WORLD_SIZE_MULTIPLICATOR, ((OrthographicCamera) stage.getCamera()).zoom + zoom));
    }

    /** Should be called when the player clicked a tile. Will place or break a block.
     * @param position The tile clicked
     * @param rightClick If the click was a right click. Left click assumed if false.
     * @return The block that was placed; null indicates the block was removed. */
    public Block mapClicked(TileVector position, boolean rightClick) {
        if (rightClick) {
            removeBlock(position);
            return null;
        } else {
            Block block = new Block(position, getPlayer().getBlockSelected(), getPlayer().getBlockDirection());
            addBlock(block);
            return block;
        }
    }

    /** Adds the block to the world.
     * @param block The block to add. */
    public void addBlock(Block block) {
        if (map.addBlock(block)) { // Return value of false indicates a block was already present
            block.registerToGroup(blockGroup);
            physics.blockPlaced(block);
        }
    }

    /** Removes a block.
     * @param position The position of the block to remove. */
    public void removeBlock(TileVector position) {
        map.removeBlock(position);
        physics.blockRemoved(position);
    }

    private void updateCamera() {
        final float zoom = ((OrthographicCamera) stage.getCamera()).zoom;
        // These values determine the min/max positions of the camera. These prevent the camera from displaying out-of-bounds areas.
        final float minCameraX = (zoom * WORLD_VIEWPORT_WIDTH) / 2f;
        final float minCameraY = (zoom * WORLD_VIEWPORT_HEIGHT) / 2f;
        final float maxCameraX = (WORLD_VIEWPORT_WIDTH * WORLD_SIZE_MULTIPLICATOR) - minCameraX;
        final float maxCameraY = (WORLD_VIEWPORT_HEIGHT * WORLD_SIZE_MULTIPLICATOR) - minCameraY;

        if (player.getPosition() != cameraPosition) { // Camera is free; needs to be conformed to bounds
            cameraPosition.x = Math.max(minCameraX, Math.min(maxCameraX, cameraPosition.x));
            cameraPosition.y = Math.max(minCameraY, Math.min(maxCameraY, cameraPosition.y));
        }

        final Vector3 position = stage.getCamera().position;
        position.x = cameraPosition.x + (player.entitySize / 2f);
        position.y = cameraPosition.y + (player.entitySize / 2f);

        // Ensure the edges of the screen will not scroll into view
        position.x = Math.max(minCameraX, Math.min(maxCameraX, position.x));
        position.y = Math.max(minCameraY, Math.min(maxCameraY, position.y));
    }

    /** The world's viewport needs to be updated as well.
     * @param height The new viewport height
     * @param width The new viewport width*/
    public void resizeViewport(int width, int height) {
        stage.getViewport().update(width, height, true);
        physics.resizeViewport(stage.getViewport());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /** Turns a vector with screen coordinates into one with corresponding world coordinates.
     * @param v The vector to transform. */
    public void screenToWorldCoordinates(Vector2 v) {
        stage.screenToStageCoordinates(v);
    }

    /** Creates a new item.
     * @param position The position of the item. Will be centered automatically.
     * @param material The type/material of the item to be spawned. */
    public void spawnItem(TileVector position, Material material) {
        Item item = new Item(new TileVector().set(position), material);
        item.registerToStage(stage);
        physics.itemAdded(item);
        items.add(item);
    }

    /** Spawn a beast.
     * @param position The position of the beast.
     * @return The beast spawned. */
    public Beast spawnBeast(TileVector position) {
        Beast beast = new Beast(new TileVector().set(position));
        addBeast(beast);
        return beast;
    }

    /** Add a beast to the world.
     * @param beast The new guy. */
    public void addBeast(Beast beast) {
        beast.registerToStage(stage);
        physics.beastAdded(beast);
        beasts.add(beast);
        beastPositions.add(beast.getPosition());
    }

    public Array<Vector2> getBeastPositions() {
        return beastPositions;
    }

    /** Update beast positions.
     * @param positions The array to copy positions from. */
    public void updateBeastPositions(Array<Vector2> positions) {
        for (int i = 0; i < beastPositions.size; i++) {
            beastPositions.get(i).set(positions.get(i));
        }
    }
}
