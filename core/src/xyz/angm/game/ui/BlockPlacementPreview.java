package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import xyz.angm.game.Game;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.blocks.BlockProperties;

/** An actor displaying a preview of the block the player has selected.
 * Will also display a blocks radius if it has one. */
public class BlockPlacementPreview extends Image {

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private BlockProperties blockProperties;

    /** Construct a new preview actor. */
    public BlockPlacementPreview() {
        setSize(1, 1);
        setColor(1, 1, 1, 0.5f);
        setOrigin(Align.center);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.4f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (blockProperties != null && blockProperties.range > -1) {
            // Batch needs to disabled temporarily for the shape renderer to do its work
            batch.end();
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(getX() + 0.5f, getY() + 0.5f, blockProperties.range, 50);
            shapeRenderer.end();
            batch.begin();
        }
        super.draw(batch, parentAlpha);
    }

    /** Update the actor.
     * @param position The position of the players cursor. Values are floored.
     * @param blockId The block id selected by the player.
     * @param rotation The rotation the player currently has selected. */
    public void update(Vector2 position, int blockId, Block.Direction rotation) {
        blockProperties = BlockProperties.getProperties(blockId);
        setPosition((int) position.x, (int) position.y);
        setRotation(rotation.toDegrees());
        setDrawable(new TextureRegionDrawable(new TextureRegion(Game.assets.get(blockProperties.getFullTexturePath(), Texture.class))));
    }
}
