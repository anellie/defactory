package xyz.angm.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import xyz.angm.game.Game;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.entities.Player;

import static xyz.angm.game.ui.Screen.VIEWPORT_HEIGHT;
import static xyz.angm.game.ui.Screen.VIEWPORT_WIDTH;
import static xyz.angm.game.world.TerrainGenerator.WORLD_SIZE_MULTIPLICATOR;
import static xyz.angm.game.world.entities.Entity.ENTITY_SIZE;

/** Represents the game world and contains all entities and the world map. */
public class World implements Disposable {

    /** Seed used for generating terrain. See {@link TerrainGenerator}. */
    public final long seed;
    private final WorldMap map;
    private final Player player = new Player();

    private final Stage stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
    private final Image selector = new Image(Game.assets.get("textures/selector.png", Texture.class));
    private final TileVector selectorPosition = new TileVector();
    private final Vector2 tmpV = new Vector2();

    /** Constructs a new world along with it's map.
     * @param seed The seed for world generation. */
    public World(long seed) {
        this.seed = seed;
        map = new WorldMap(new TerrainGenerator(seed));

        stage.addActor(map);
        stage.addActor(selector);
        player.registerToStage(stage);
    }

    /** Returns the player entity.
     * @return The player */
    public Player getPlayer() {
        return player;
    }

    /** Should be called every frame on the server so the world can update.
     * @param delta Time since last call to this method in seconds. */
    public void act(float delta) {
        player.act(delta);

        tmpV.set(Gdx.input.getX(), Gdx.input.getY());
        stage.screenToStageCoordinates(tmpV);
        selectorPosition.set(tmpV);
        selector.setPosition(selectorPosition.getX(), selectorPosition.getY());
    }

    /** Should be called every frame when the world should render itself and all components.
     * @param delta Time since last call to this method in seconds. */
    public void render(float delta) {
        updateCamera();
        stage.draw();
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
            Block block = new Block(position);
            addBlock(block);
            return block;
        }
    }

    /** Adds the block to the world.
     * @param block The block to add. */
    public void addBlock(Block block) {
        map.addBlock(block);
        block.registerToStage(stage);
    }

    /** Removes a block.
     * @param position The position of the block to remove. */
    public void removeBlock(TileVector position) {
        map.removeBlock(position);
    }

    private void updateCamera() {
        final float zoom = ((OrthographicCamera) stage.getCamera()).zoom;
        // These values determine the min/max positions of the camera. These prevent the camera from displaying out-of-bounds areas.
        final float minCameraX = (zoom * VIEWPORT_WIDTH) / 2f;
        final float minCameraY = (zoom * VIEWPORT_HEIGHT) / 2f;
        final float maxCameraX = (VIEWPORT_WIDTH * WORLD_SIZE_MULTIPLICATOR) - minCameraX;
        final float maxCameraY = (VIEWPORT_HEIGHT * WORLD_SIZE_MULTIPLICATOR) - minCameraY;

        final Vector3 position = stage.getCamera().position;
        position.x = player.getPosition().x + (ENTITY_SIZE / 2f);
        position.y = player.getPosition().y + (ENTITY_SIZE / 2f);

        // Ensure the edges of the screen will not scroll into view
        position.x = Math.max(minCameraX, Math.min(maxCameraX, position.x));
        position.y = Math.max(minCameraY, Math.min(maxCameraY, position.y));
    }

    /** The world's viewport needs to be updated as well.
     * @param height The new viewport height
     * @param width The new viewport width*/
    public void resizeViewport(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /** Turns a vector with screen coordinates into one with corresponding world coordinates.
     * @param v The vector to transform.
     * @return The transformed vector given as an argument. */
    public Vector2 screenToWorldCoordinates(Vector2 v) {
        stage.screenToStageCoordinates(v);
        return v;
    }
}
