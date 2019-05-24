package xyz.angm.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import xyz.angm.game.Game;
import xyz.angm.game.network.Client;
import xyz.angm.game.network.NetworkInterface;
import xyz.angm.game.ui.BlockPlacementPreview;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.blocks.BlockTickRunner;
import xyz.angm.game.world.blocks.BlockType;
import xyz.angm.game.world.blocks.Material;
import xyz.angm.game.world.entities.Beast;
import xyz.angm.game.world.entities.Bullet;
import xyz.angm.game.world.entities.Item;
import xyz.angm.game.world.entities.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private final NetworkInterface netIface = ((Game) Gdx.app.getApplicationListener()).getNetworkInterface();
    private final Player player = new Player();
    private final Array<Item> items = new Array<>(false, 16);
    private final Array<Beast> beasts = new Array<>(true, 16);
    private final Array<Vector2> beastPositions = new Array<>(true, 16);
    private final Array<Bullet> bullets = new Array<>(false, 32);
    private final ScheduledExecutorService bulletTimer = Executors.newSingleThreadScheduledExecutor();
    private final PhysicsEngine physics;
    private int beastsLeft = 0;

    private final Stage stage = new Stage(new FitViewport(WORLD_VIEWPORT_WIDTH, WORLD_VIEWPORT_HEIGHT));
    private final Vector2 cameraPosition;
    private final Group blockGroup = new Group();
    private final BlockPlacementPreview blockPreview = new BlockPlacementPreview();
    private final Vector2 tmpV = new Vector2();

    /** Constructs a new world along with it's map.
     * @param generator The generator which is done loading.
     * @param active If false, background activity is heavily restricted and the world won't generate a CORE. Used on the client. */
    public World(TerrainGenerator generator, boolean active) {
        this.seed = generator.seed;
        map = new WorldMap(generator);
        physics = new PhysicsEngine(this, active);

        if (active) {
            // Schedule the block ticker to run every BLOCK_TICK_FREQ
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(new BlockTickRunner(this), BLOCK_TICK_FREQ, BLOCK_TICK_FREQ, TimeUnit.MILLISECONDS);
            cameraPosition = player.getPosition(); // Lock camera on player
        } else {
            // Free the camera from the player. Allows moving it independently from the player for spectators.
            cameraPosition = player.getPosition().cpy();
        }

        addBlock(player.getCore());
        stage.addActor(map);
        stage.addActor(blockGroup);
        player.registerToStage(stage);
        stage.addActor(blockPreview);
        ((OrthographicCamera) stage.getCamera()).zoom = 0.2f;
    }

    /** Call when the wave status changed, eg on wave start or end.
     * @param status The new status. */
    public void waveStatusChanged(Client.Status status) {
        if (status == Client.Status.WAVE_START) beastsLeft = 3 + player.getBeastWave();
        else if (status == Client.Status.WAVE_END) beastsLeft = 0;
    }

    /** Should be called every frame on the server so the world can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        physics.act(delta);
        player.act(delta);
        stage.act(delta);
        items.forEach(item -> item.act(delta));
        beasts.forEach(beast -> beast.act(delta));
        bullets.forEach(bullet -> bullet.act(delta));
    }

    /** Should be called every frame when the world should render itself and all components. */
    public void render() {
        updateCamera();
        stage.draw();
        physics.render((OrthographicCamera) stage.getCamera());
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
        if (getPlayer().getBlockSelected() == -1) return;
        tmpV.set(x, y);
        stage.screenToStageCoordinates(tmpV);
        blockPreview.update(tmpV, getPlayer().getBlockSelected(), getPlayer().getBlockDirection());
    }

    /** Zooms the world map; scaling it bigger or smaller.
     * @param zoom The zoom amount. */
    public void zoomMap(float zoom) {
        // Limit the zoom from 0 to world size to prevent unwanted behavior
        ((OrthographicCamera) stage.getCamera()).zoom =
                Math.max(0, Math.min(WORLD_SIZE_MULTIPLICATOR, ((OrthographicCamera) stage.getCamera()).zoom + zoom));
    }

    /** Should be called when the player clicked the map/screen. Will place or break a block and sync to clients.
     * @param x The x position of the click in screen coordinates.
     * @param y The y position of the click in screen coordinates.
     * @param rightClick If the click was a right click. Left click assumed if false. */
    public void mapClicked(int x, int y, boolean rightClick) {
        tmpV.set(x, y);
        stage.screenToStageCoordinates(tmpV);
        TileVector position = new TileVector().set(tmpV);

        if (rightClick) {
            removeBlock(position);
            netIface.send(position);
        } else if (getPlayer().getBlockSelected() != -1) {
            Block block = getPlayer().buildBlock(position);
            if (block == null) return;
            addBlock(block);
            netIface.send(block);
        }
    }

    /** Should be called when a spectator clicked the map/screen. Will tell the server to spawn a beast at the clicked position.
     * @param x The x position of the click in screen coordinates.
     * @param y The y position of the click in screen coordinates. */
    public void requestBeastSpawn(int x, int y) {
        if (beastsLeft < 1) return;
        tmpV.set(x, y);
        stage.screenToStageCoordinates(tmpV);
        TileVector position = new TileVector().set(tmpV);

        // Prevent spawning a beast within the middle of the screen, beasts could be spawned unfairly otherwise
        if (!position.isInBounds((int) (WORLD_VIEWPORT_WIDTH / 3), (int) ((WORLD_VIEWPORT_WIDTH / 3) * 2))) {
            netIface.send(position);
            beastsLeft--;
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
        Block block = map.getBlock(position);
        if (block != null && block.getProperties().type != BlockType.CORE) {
            map.removeBlock(position);
            physics.blockRemoved(position);
        }
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

    /** Creates a new item.
     * @param position The position of the item. Will be centered automatically.
     * @param material The type/material of the item to be spawned. */
    public void spawnItem(TileVector position, Material material) {
        Item item = new Item(new TileVector().set(position), material);
        item.registerToStage(stage);
        physics.itemAdded(item);
        items.add(item);
    }

    /** Call when an item should be removed.
     * @param item The item to remove. */
    void removeItem(Item item) {
        item.dispose();
        physics.itemRemoved(item);
        items.removeValue(item, true);
    }

    /** Spawn a beast and sync to clients.
     * @param position The position of the beast. */
    public void spawnBeast(TileVector position) {
        Beast beast = new Beast(new TileVector().set(position));
        addBeast(beast);
        netIface.send(beast);
    }

    /** Add a beast to the world.
     * @param beast The new guy. */
    public void addBeast(Beast beast) {
        beast.registerToStage(stage);
        physics.beastAdded(beast);
        beasts.add(beast);
        beastPositions.add(beast.getPosition());
    }

    /** Remove a beast from the world.
     * @param beast The beast to get rid of. */
    public void removeBeast(Beast beast) {
        beast.removeHealth(999); // Make sure its dead so clients will remove it as well
        beast.dispose();
        beasts.removeValue(beast, true);
        beastPositions.removeValue(beast.getPosition(), true);
        physics.beastRemoved(beast);
        netIface.send(beast);
    }

    /** Spawn a new bullet shot by a turret.
     * @param turret The position of the turret that shot.
     * @param target The target location of the bullet. */
    public void spawnBullet(TileVector turret, Vector2 target) {
        Bullet bullet = new Bullet(turret, target);
        bullet.registerToStage(stage);
        physics.bulletAdded(bullet);
        bullets.add(bullet);

        // Remove the bullet again after a delay
        bulletTimer.schedule(() -> removeBullet(bullet), 30, TimeUnit.SECONDS);
    }

    /** Remove a bullet. Called when it hits a beast, or after 10sec of being active.
     * @param bullet The bullet to remove. */
    void removeBullet(Bullet bullet) {
        bullets.removeValue(bullet, true);
        physics.bulletRemoved(bullet);
        bullet.dispose();
    }

    public Array<Beast> getBeasts() {
        return beasts;
    }

    public Array<Vector2> getBeastPositions() {
        return beastPositions;
    }

    /** Returns the player entity.
     * @return The player */
    public Player getPlayer() {
        return player;
    }

    /** Returns the amount of beasts allowed to be spawned by this spectator at the moment. */
    public int getBeastsLeft() {
        return beastsLeft;
    }

    /** Update beast positions.
     * @param positions The array to copy positions from. */
    public void updateBeastPositions(Array<Vector2> positions) {
        for (int i = 0; i < beastPositions.size; i++) {
            beastPositions.get(i).set(positions.get(i));
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        physics.dispose();
    }
}
