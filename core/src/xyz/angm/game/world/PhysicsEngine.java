package xyz.angm.game.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.*;
import xyz.angm.game.world.entities.Player;

import java.util.HashMap;

import static xyz.angm.game.world.WorldMap.TILE_SIZE;
import static xyz.angm.game.world.entities.Entity.ENTITY_SIZE;

/** A simple physics 'engine' wrapping the Box2D physics library.
 * See the LibGDX wiki for explanations of the different parts of Box2D: https://github.com/libgdx/libgdx/wiki/box2d */
class PhysicsEngine {

    /** The step size of every Box2D engine step. */
    private static final float TIME_STEP = 1/60f;
    // All constants below are for converting between scale. Box2D recommends bodies to be 0.1-10 units big; entities are bigger and need scaling
    private static final float PHYSICS_TO_WORLD_SCALE = TILE_SIZE;
    private static final float WORLD_TO_PHYSICS_SCALE = 1 / PHYSICS_TO_WORLD_SCALE;
    private static final float PHYSICS_TILE_SIZE = TILE_SIZE / 2f * WORLD_TO_PHYSICS_SCALE;
    private static final float PHYSICS_ENTITY_SIZE = ENTITY_SIZE / 2.075f * WORLD_TO_PHYSICS_SCALE;

    private final World pWorld = new World(new Vector2(0, 0), true);
    private final HashMap<TileVector, Body> blocks = new HashMap<>();
    private final Body playerBody;
    private float timeSinceLastStep = 0f;

    private final BodyDef blockDef = new BodyDef();
    private final Vector2 tmpV = new Vector2();

    /** Construct a new engine.
     * @param player The player the system should act on. */
    PhysicsEngine(Player player) {
        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(player.getPosition()).add(ENTITY_SIZE / 2f, ENTITY_SIZE / 2f).scl(WORLD_TO_PHYSICS_SCALE);

        Body pBody = pWorld.createBody(playerDef);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(PHYSICS_ENTITY_SIZE, PHYSICS_ENTITY_SIZE);

        FixtureDef playerFixDef = new FixtureDef();
        playerFixDef.shape = playerShape;
        playerFixDef.density = 0.5f;
        playerFixDef.friction = 0.4f;
        playerFixDef.restitution = 0.6f;
        pBody.createFixture(playerFixDef);
        pBody.setFixedRotation(true);

        pBody.setUserData(player);
        this.playerBody = pBody;
        playerShape.dispose();
    }

    /** Advances the physics engine. Should be called every frame.
     * @param deltaTime Time since last call */
    void act(float deltaTime) {
        Player player = (Player) playerBody.getUserData();

        // Update player velocity from player input
        playerBody.setLinearVelocity(tmpV.set(player.getVelocity()).scl(player.getMovementMultiplier()));

        // Step physics engine
        float frameTime = Math.min(deltaTime, 0.25f);
        timeSinceLastStep += frameTime;
        while (timeSinceLastStep >= TIME_STEP) {
            pWorld.step(TIME_STEP, 6, 2);
            timeSinceLastStep -= TIME_STEP;
        }

        // Update player position from physics simulation
        player.getPosition().set(playerBody.getPosition().scl(PHYSICS_TO_WORLD_SCALE).sub(ENTITY_SIZE / 2, ENTITY_SIZE / 2));
    }

    /** Call when a block was placed. Will add the block to the physics simulation.
     * Blocks that can be walked through should NOT be part of the simulation; and not added with this method.
     * @param position The position of the block. */
    void blockPlaced(TileVector position) {
        blockDef.position.set(position.getX(), position.getY()).add(TILE_SIZE / 2f, TILE_SIZE / 2f).scl(WORLD_TO_PHYSICS_SCALE);
        Body blockBody = pWorld.createBody(blockDef);

        PolygonShape blockShape = new PolygonShape();
        blockShape.setAsBox(PHYSICS_TILE_SIZE, PHYSICS_TILE_SIZE);
        blockBody.createFixture(blockShape, 0f);
        blockShape.dispose();
        blocks.put(position, blockBody);
    }

    /** Call when a block was removed. Will remove the block from the physics simulation.
     * Blocks that can be walked through are NOT part of the simulation; and do not need to be removed with this method.
     * @param position The position of the block removed. */
    void blockRemoved(TileVector position) {
        Body block = blocks.remove(position);
        if (block != null) pWorld.destroyBody(block);
    }
}