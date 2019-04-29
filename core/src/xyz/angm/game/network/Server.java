package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** A server capable of connecting to an unlimited amount of clients.
 * The player trying to defend themselves from beasts is the one hosting the server.
 * Automatically starts a server discovery thread for clients to find. */
public class Server extends NetworkInterface {

    private final com.esotericsoftware.kryonet.Server kryoServer = new com.esotericsoftware.kryonet.Server();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean start() {
        Thread discoveryThread = new Thread(new ServerDiscoveryRunnable());
        discoveryThread.start();

        kryoServer.start();
        registerClasses(kryoServer.getKryo());

        try {
            kryoServer.bind(PORT);
            setupListeners();
        } catch (IOException e) {
            Gdx.app.error("Server", "Could not open port! Game already running?");
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
                // TODO Handle connecting players
            }

            @Override
            public void received(Connection connection, Object object) {
                // TODO Handle receiving packets
            }
        });

        scheduler.scheduleAtFixedRate(this::updateClientsEntities, 50, 50, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::updateClientsMap, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    // Update entities on all clients connected to this server
    private void updateClientsEntities() {
        // TODO
    }

    // Update map (placed blocks) on all clients connected to this server
    private void updateClientsMap() {
        // TODO
    }
}
