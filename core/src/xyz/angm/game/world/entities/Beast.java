package xyz.angm.game.world.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.VisUI;
import xyz.angm.game.world.TileVector;

/** Represents an enemy the player is trying to defend against.
 * The goal of beasts is to destroy the player's base, attacking in waves. */
public class Beast extends Entity {

    /** The maximum beast health. */
    private static final int BEAST_HEALTH = 20;

    private final transient Vector2 tmpV = new Vector2();
    private final transient HealthBar healthBar = new HealthBar();

    /** Required for kryo deserialization; needs a no-arg constructor. */
    private Beast() {
        super(1);
        health = BEAST_HEALTH;
        actorTexture = "textures/beast.png";
    }

    /** Constructs a beast. Requires AssetManager in Game to be ready.
     * @param position The position of the beast. */
    public Beast(TileVector position) {
        this();
        getPosition().set(position.getX(), position.getY());
    }

    /** Get the location the beast would like to be at.
     * @param player The player this beast is targeting.
     * @return The location the beast wants to move to. Usually the CORE. */
    public Vector2 getTargetLocation(Player player) {
        tmpV.set(player.getCore().getPosition().getX(), player.getCore().getPosition().getY());
        tmpV.sub(getPosition());
        tmpV.limit(1f);
        return tmpV;
    }

    /** Subtract from the health of the beast.
     * @param amount The amount of health lost. */
    public void removeHealth(int amount) {
        health -= amount;
    }

    @Override
    public void registerToStage(Stage stage) {
        super.registerToStage(stage);
        stage.addActor(healthBar);
    }

    @Override
    public void dispose() {
        super.dispose();
        healthBar.remove();
    }

    /** A simple health bar displaying at the bottom of a beast. */
    private class HealthBar extends Actor {

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (health == BEAST_HEALTH) return; // Bar is full, don't show
            VisUI.getSkin().getDrawable("black-transparent").draw(batch, getPosition().x, getPosition().y, 1f, 0.1f);
            VisUI.getSkin().getDrawable("green").draw(batch, getPosition().x, getPosition().y, (float) health / BEAST_HEALTH, 0.1f);
        }
    }
}