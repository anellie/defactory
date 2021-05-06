package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import xyz.angm.game.Defactory;
import xyz.angm.game.ui.screens.GameScreen;
import xyz.angm.game.ui.screens.MapLoadingScreen;
import xyz.angm.game.world.TileVector;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static xyz.angm.game.network.Client.Status;

/** A server capable of connecting to an unlimited amount of clients.
 * The player trying to defend themselves from beasts is the one hosting the server.
 * Automatically starts a server discovery thread for clients to find. */
public class Server extends NetworkInterface {

    private final com.esotericsoftware.kryonet.Server kryoServer = new com.esotericsoftware.kryonet.Server();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Defactory game;

    /** Create a new server.
     * @param game The game to use for syncing with clients. */
    public Server(Defactory game) {
        this.game = game;
    }

    @Override
    public boolean start() {
        new Thread(new ServerDiscoveryRunnable()).start();

        try {
            kryoServer.start();
            registerClasses(kryoServer.getKryo());
            kryoServer.bind(PORT);
            setupListeners();
        } catch (IOException e) {
            Gdx.app.error("Server", "Could not open port! Game already running?", e);
            return false;
        }
        return true;
    }

    @Override
    public void send(Object toSend) {
        kryoServer.sendToAllTCP(toSend);
    }

    @Override
    public void dispose() {
        kryoServer.close();
    }

    // Setup all required listeners
    private void setupListeners() {
        kryoServer.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                if (game.getScreen() instanceof MapLoadingScreen) connection.close(); // Not ready yet!

                // Sync world seed to client
                connection.sendTCP(((GameScreen) game.getScreen()).getWorld().seed);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof TileVector) {
                    ((GameScreen) game.getScreen()).getWorld().spawnBeast((TileVector) object);
                }
            }
        });

        scheduler.scheduleAtFixedRate(this::updateClientsEntities, 50, 50, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::startWave, 180, 90, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(this::endWave, 210, 90, TimeUnit.MINUTES);
    }

    // Update entities on all clients connected to this server
    private void updateClientsEntities() {
        if (game.getScreen() instanceof GameScreen) {
            send(((GameScreen) game.getScreen()).getWorld().getPlayer());
            send(((GameScreen) game.getScreen()).getWorld().getBeastPositions());
        }
    }

    private void startWave() {
        ((GameScreen) game.getScreen()).waveBegun();
        send(Status.WAVE_START);
    }

    private void endWave() {
        send(Status.WAVE_END);
    }
}
