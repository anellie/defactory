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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.blocks.BlockType;
import xyz.angm.game.world.entities.Beast;
import xyz.angm.game.world.entities.Item;
import xyz.angm.game.world.entities.Player;

/** A 'simple' physics 'engine' wrapping the Box2D physics library.
 * See the LibGDX wiki for explanations of the different parts of Box2D: https://github.com/libgdx/libgdx/wiki/box2d */
class PhysicsEngine implements Disposable {

    /** The step size of every Box2D engine step. */
    private static final float TIME_STEP = 1 / 60f;
    /** Color of the light source of BlockType.TORCH blocks. */
    private static final Color TORCH_LIGHT_COLOR = new Color(0xFF8D0099);
    /** Half the size of items. */
    private static final float ITEM_SIZE = 0.25f;
    /** Size of sensor blocks. Smaller to improve interactions between conveyors and items. */
    private static final float SENSOR_BODY_SIZE = 0.25f;

    private final xyz.angm.game.world.World gameWorld;
    private final World pWorld = new World(new Vector2(0, 0), true);
    private final WorldContactListener contactListener = new WorldContactListener();
    private final ObjectMap<TileVector, Body> blocks = new ObjectMap<>();
    private final Array<Body> items = new Array<>();
    private final Array<Body> beasts = new Array<>();
    private final Body playerBody;
    private final RayHandler rayHandler = new RayHandler(pWorld);
    private final ObjectMap<Body, Light> blockLights = new ObjectMap<>();
    private final boolean authority;

    private float timeSinceLastStep = 0f;
    private final BodyDef bodyDef = new BodyDef();
    private final Vector2 tmpV = new Vector2();

    /** Construct a new engine.
     * @param world The game world.
     * @param authority Whether or not the system is the source of truth.
     *                  If false, positions will be forced from the entities and the engine
     *                  will act purely as a lighting engine. */
    PhysicsEngine(xyz.angm.game.world.World world, boolean authority) {
        this.gameWorld = world;
        this.authority = authority;
        Player player = world.getPlayer();
        this.playerBody = createBody(BodyDef.BodyType.DynamicBody, player, player.getPosition(),
                player.entitySize / 2.1f, 1f, 0.4f, 0.6f, false);

        pWorld.setContactListener(contactListener);

        rayHandler.setAmbientLight(0f, 0f, 0f, 0.3f);
        PointLight playerLight = new PointLight(rayHandler, 128, new Color(1f, 1f, 1f, 0.5f), 10, 0, 0);
        playerLight.attachToBody(playerBody);
    }

    /** Updates the physics engine. Should be called every frame.
     * @param deltaTime Time since last call */
    void act(float deltaTime) {
        if (authority) stepEngine(deltaTime);
        else {
            playerBody.setTransform(((Player) playerBody.getUserData()).getPosition(), 0);
            beasts.forEach(beastBody -> beastBody.setTransform(((Beast) beastBody.getUserData()).getPosition(), 0));
        }
    }

    /** Renders the lighting parts of the world, using Box2DLights.
     * @param camera The camera to use for rendering. */
    void render(OrthographicCamera camera) {
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }

    /** Step the engine. Only called when engine is authority. */
    private void stepEngine(float deltaTime) {
        Player player = (Player) playerBody.getUserData();

        // Update velocities
        playerBody.setLinearVelocity(tmpV.set(player.getVelocity()).scl(player.getMovementMultiplier()));
        beasts.forEach(beast -> beast.setLinearVelocity(((Beast) beast.getUserData()).getTargetLocation(player)));

        // Step physics engine
        float frameTime = Math.min(deltaTime, 0.25f);
        timeSinceLastStep += frameTime;
        while (timeSinceLastStep >= TIME_STEP) {
            contactListener.step();
            pWorld.step(TIME_STEP, 6, 2);
            timeSinceLastStep -= TIME_STEP;
        }

        // Update positions from physics simulation; also set item velocity to 0 to prevent buildup
        player.getPosition().set(playerBody.getPosition().sub(player.entitySize / 2f, player.entitySize / 2f));
        beasts.forEach(beastBody -> ((Beast) beastBody.getUserData()).getPosition().set(beastBody.getPosition().sub(0.5f, 0.5f)));
        items.forEach(item -> {
            if (item.getUserData() == "DESTROY") { // Remove all items marked for deletion
                pWorld.destroyBody(item);
                items.removeValue(item, true);
            } else {
                ((Item) item.getUserData()).getPosition().set(item.getPosition().sub(ITEM_SIZE, ITEM_SIZE));
                item.setLinearVelocity(0, 0);
            }
        });
    }

    private Body createBody(BodyDef.BodyType type, Object userData, Vector2 position, float size,
                            float density, float friction, float restitution, boolean sensor) {
        bodyDef.position.set(position).add(size, size);
        bodyDef.fixedRotation = true;
        Body body = pWorld.createBody(bodyDef);
        body.setType(type);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size, size);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = density;
        fixDef.friction = friction;
        fixDef.restitution = restitution;
        fixDef.isSensor = sensor;

        body.createFixture(fixDef);
        body.setUserData(userData);
        shape.dispose();
        return body;
    }

    /** Call when a block was placed. Will add the block to the physics simulation.
     * Blocks that can be walked through should NOT be part of the simulation; and not added with this method.
     * @param block The block added to the world. */
    void blockPlaced(Block block) {
        block.getPosition().setToItself(tmpV);
        if (block.getProperties().isSensor) tmpV.add(0.5f - SENSOR_BODY_SIZE, 0.5f - SENSOR_BODY_SIZE);
        Body blockBody = createBody(BodyDef.BodyType.StaticBody, block, tmpV,
                block.getProperties().isSensor ? SENSOR_BODY_SIZE : 0.5f, 0f, 0.2f, 0f, block.getProperties().isSensor);
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
        if (((Block) block.getUserData()).getProperties().type == BlockType.TORCH) blockLights.remove(block).remove(true);
        pWorld.destroyBody(block);
    }

    /** Call when an item has been added to the world.
     * @param item The item to add. */
    void itemAdded(Item item) {
        Body itemBody = createBody(BodyDef.BodyType.DynamicBody, item, item.getPosition().add(ITEM_SIZE, ITEM_SIZE), ITEM_SIZE, 1f, 0.8f, 0f, true);
        items.add(itemBody);
    }

    /** Call when an item was removed from the world.
     * @param item The item removed. */
    void itemRemoved(Item item) {
        for (Body body : items) {
            if (body.getUserData() == item) {
                body.setUserData("DESTROY");
                return;
            }
        }
    }

    /** Call when a beast was added to the world.
     * @param beast The beast to add. */
    void beastAdded(Beast beast) {
        Body beastBody = createBody(BodyDef.BodyType.DynamicBody, beast, beast.getPosition(),
                beast.entitySize / 2f, 0.8f, 0.6f, 0.6f, false);
        beasts.add(beastBody);
    }

    /** Call when a beast was removed from the world.
     * @param beast The beast removed. */
    void beastRemoved(Beast beast) {
        for (Body body : beasts) {
            if (body.getUserData() == beast) {
                pWorld.destroyBody(body);
                beasts.removeValue(body, true);
                return;
            }
        }
    }

    /** Call when viewport size changed. Needs to be independent since RayHandler changes the viewport on its own otherwise.
     * @param viewport The viewport post-change */
    void resizeViewport(Viewport viewport) {
        rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
    }

    @Override
    public void dispose() {
        pWorld.dispose();
    }

    /** Listens for contacts between entities and handles all contact-based interactions. */
    private class WorldContactListener implements ContactListener {

        private static final float CONVEYOR_BELT_IMPULSE = 1f;
        private static final float CONVEYOR_BELT_PULL = 2f;

        private final Vector2 tmpV = new Vector2();
        private final Array<Block> deadBlocks = new Array<>(false, 2);

        private void step() {
            pWorld.getContactList().forEach(this::processContact);
            deadBlocks.forEach(block -> gameWorld.removeBlock(block.getPosition()));
            deadBlocks.clear();
        }

        private void processContact(Contact contact) {
            if (!contact.isTouching()) return; // Filter out AABB phase contacts

            Body b1 = contact.getFixtureA().getBody();
            Body b2 = contact.getFixtureB().getBody();

            if (b1.getUserData() instanceof Block || b2.getUserData() instanceof Block) {
                Block block = (Block) ((b1.getUserData() instanceof Block) ? b1.getUserData() : b2.getUserData());
                Body blockBody = (b1.getUserData() instanceof Block) ? b1 : b2;
                Body otherBody = (blockBody == b1) ? b2 : b1;
                processBlock(block, blockBody, otherBody);
            } else if (b1.getUserData() instanceof Item || b2.getUserData() instanceof Item) {
                Item item = (Item) ((b1.getUserData() instanceof Item) ? b1.getUserData() : b2.getUserData());
                Body otherBody = (b1.getUserData() instanceof Item) ? b2 : b1;
                processItem(item, otherBody);
            }
        }

        // Process contact between a block and another body
        private void processBlock(Block block, Body blockBody, Body otherBody) {
            if (otherBody.getUserData() instanceof Beast) {
                processBlockAndBeast(block);
            } else if (block.getProperties().type == BlockType.CONVEYOR) {
                processConveyor(block, blockBody, otherBody);
            } else if (otherBody.getUserData() instanceof Item) {
                processBlockAndItem((Item) otherBody.getUserData(), block);
            }
        }

        // Process contact between a conveyor and another body
        private void processConveyor(Block conveyor, Body conveyorBody, Body otherBody) {
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

            // The body is pulled to the center of the conveyor first. This prevents the body from flying off the side
            // when conveyors change direction. (Also creates a nice looking push animation)
            otherBody.applyLinearImpulse(
                    conveyorBody.getPosition().sub(otherBody.getPosition()).scl(CONVEYOR_BELT_PULL),
                    otherBody.getPosition(), true);
            otherBody.applyLinearImpulse(tmpV, otherBody.getPosition(), true);
        }

        // Process contact between an item and another body
        private void processItem(Item item, Body otherBody) {
            if (otherBody.getUserData() instanceof Player) {
                processPlayerAndItem(item, (Player) otherBody.getUserData());
            }
        }

        private void processPlayerAndItem(Item item, Player player) {
            player.inventory.add(item.material, 1);
            gameWorld.removeItem(item);
        }

        private void processBlockAndItem(Item item, Block block) {
            if (block.getProperties().materialRequired == item.material) {
                block.incrementMaterial();
                gameWorld.removeItem(item);
            }
        }

        private void processBlockAndBeast(Block block) {
            block.onHit();
            if (block.getHealth() <= 0) deadBlocks.add(block);
        }

        @Override
        public void beginContact(Contact contact) {
            processContact(contact);
        }

        @Override
        public void endContact(Contact contact) {
            // Not needed; interface requires it to be implemented
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