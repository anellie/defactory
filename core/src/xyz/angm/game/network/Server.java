package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

/** A server capable of connecting to an unlimited amount of clients.
 * The player trying to defend themselves from beasts is the one hosting the server.
 * Automatically starts a server discovery thread for clients to find. */
public class Server extends NetworkInterface {

    private com.esotericsoftware.kryonet.Server kryoServer;

    /** Will start the server along with a discovery thread to find it. */
    public void start() {
        Thread discoveryThread = new Thread(new ServerDiscoveryRunnable());
        discoveryThread.start();

        kryoServer = new com.esotericsoftware.kryonet.Server();
        kryoServer.start();
        registerClasses(kryoServer.getKryo());

        try {
            kryoServer.bind(PORT);
        } catch (IOException e) {
            Gdx.app.error("Server", "Could not open port! Game already running?");
            System.exit(-1); // TODO display GUI message instead of just logging it
        }

        kryoServer.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                // TODO Handle connecting players
            }
        });
    }

    @Override
    public void send(Object toSend) {
        kryoServer.sendToAllTCP(toSend);
    }

    @Override
    public void dispose() {
        kryoServer.close();
    }
}
