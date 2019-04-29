package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class Server extends NetworkInterface {

    private com.esotericsoftware.kryonet.Server kryoServer;

    @Override
    public void start() {
        Thread discoveryThread = new Thread(new ServerDiscoveryRunnable());
        discoveryThread.start();

        kryoServer = new com.esotericsoftware.kryonet.Server();
        kryoServer.start();
        kryoServer.addListener(listener);
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

    public void send(Object toSend) {
        kryoServer.sendToAllTCP(toSend);
    }

    public void disconnect() {
        kryoServer.close();
    }
}
