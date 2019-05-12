package xyz.angm.game.world;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import xyz.angm.game.world.entities.Player;

import java.util.HashMap;

/** A simple physics 'engine' wrapping the Box2D physics library.
 * See the LibGDX wiki for explanations of the different parts of Box2D: https://github.com/libgdx/libgdx/wiki/box2d */
class PhysicsEngine {

    /** The step size of every Box2D engine step. */
    private static final float TIME_STEP = 1/60f;
    /** Scaling for the player hitbox. 2f is 1:1, hitbox needs to be slightly smaller however.*/
    private static final float PLAYER_SCALE = 2.08f;
    /** Color of the light source of BlockType.TORCH blocks. */
    private static final Color TORCH_LIGHT_COLOR = new Color(0xFF8D0099);

    private final World pWorld = new World(new Vector2(0, 0), true);
    private final WorldContactListener contactListener = new WorldContactListener();
    private final HashMap<TileVector, Body> blocks = new HashMap<>();
    private final Body playerBody;
    private final RayHandler rayHandler = new RayHandler(pWorld);
    private final HashMap<Body, Light> blockLights = new HashMap<>();

    private float timeSinceLastStep = 0f;
    private final BodyDef blockDef = new BodyDef();
    private final Vector2 tmpV = new Vector2();

    /** Construct a new engine.
     * @param player The player the system should act on. */
    PhysicsEngine(Player player) {
        pWorld.setContactListener(contactListener);

        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(player.getPosition()).add(player.entitySize / 2f, player.entitySize / 2f);

        Body pBody = pWorld.createBody(playerDef);
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(player.entitySize / PLAYER_SCALE, player.entitySize / PLAYER_SCALE);

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

        rayHandler.setAmbientLight(0f, 0f, 0f, 0.4f);
        PointLight playerLight = new PointLight(rayHandler, 128, new Color(1f, 1f, 1f, 0.75f), 10, 0, 0);
        playerLight.attachToBody(playerBody);
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
            contactListener.step();
            pWorld.step(TIME_STEP, 6, 2);
            timeSinceLastStep -= TIME_STEP;
        }

        // Update player position from physics simulation
        player.getPosition().set(playerBody.getPosition().sub(player.entitySize / 2f, player.entitySize / 2f));
    }

    /** Renders the lighting parts of the world, using Box2DLights.
     * @param camera The camera to use for rendering. */
    void render(OrthographicCamera camera) {
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    /** Call when a block was placed. Will add the block to the physics simulation.
     * Blocks that can be walked through should NOT be part of the simulation; and not added with this method.
     * @param block The block added to the world. */
    void blockPlaced(Block block) {
        blockDef.position.set(block.getPosition().getX(), block.getPosition().getY()).add(0.5f, 0.5f);
        Body blockBody = pWorld.createBody(blockDef);

        PolygonShape blockShape = new PolygonShape();
        blockShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape= blockShape;
        fixDef.density = 0f;
        fixDef.isSensor = block.getProperties().isSensor;

        blockBody.createFixture(fixDef);
        blockBody.setUserData(block);
        blockShape.dispose();
        blocks.put(block.getPosition(), blockBody);

        if (block.getProperties().type == BlockType.TORCH) {
            PointLight torchLight = new PointLight(rayHandler, 16, TORCH_LIGHT_COLOR, 10, 0, 0);
            torchLight.attachToBody(blockBody);
            blockLights.put(blockBody, torchLight);
        }
    }

    /** Call when a block was removed. Will remove the block from the physics simulation.
     * Blocks that can be walked through are NOT part of the simulation; and do not need to be removed with this method.
     * @param position The position of the block removed. */
    void blockRemoved(TileVector position) {
        Body block = blocks.remove(position);
        if (block != null) {
            if (((Block) block.getUserData()).getProperties().type == BlockType.TORCH) blockLights.remove(block).remove(true);
            pWorld.destroyBody(block);
        }
    }

    /** Listens for contacts between entities and handles all contact-based interactions. */
    private class WorldContactListener implements ContactListener {

        private static final float CONVEYOR_BELT_IMPULSE = 3f;

        private final Array<Contact> contacts = new Array<>(false, 5);
        private final Vector2 tmpV = new Vector2();

        private void step() {
            for (Contact contact : contacts) {
                Body b1 = contact.getFixtureA().getBody();
                Body b2 = contact.getFixtureB().getBody();

                if (((b1.getUserData() instanceof Block) && ((Block) b1.getUserData()).getProperties().type == BlockType.CONVEYOR) || // im so sorry
                        ((b2.getUserData() instanceof Block) && ((Block) b2.getUserData()).getProperties().type == BlockType.CONVEYOR) ) {
                    Body onConveyor = (b1.getUserData() instanceof Block) ? b2 : b1;
                    Block conveyor = (Block) ((b1.getUserData() instanceof Block) ? b1.getUserData() : b2.getUserData());

                    switch (conveyor.getDirection()) {
                        case DOWN:
                            tmpV.set(0, -CONVEYOR_BELT_IMPULSE);
                            break;
                        case UP:
                            tmpV.set(0, CONVEYOR_BELT_IMPULSE);
                            break;
                        case LEFT:
                            tmpV.set(-CONVEYOR_BELT_IMPULSE, 0);
                            break;
                        case RIGHT:
                            tmpV.set(CONVEYOR_BELT_IMPULSE, 0);
                            break;
                    }

                    onConveyor.applyLinearImpulse(tmpV, onConveyor.getPosition(), true);
                }
            }
        }

        @Override
        public void beginContact(Contact contact) {
            contacts.add(contact);
        }

        @Override
        public void endContact(Contact contact) {
            contacts.removeValue(contact, true);
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
            // Not needed; interface requires it to be implemented
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
            // Not needed; interface requires it to be implemented
        }
    }
}