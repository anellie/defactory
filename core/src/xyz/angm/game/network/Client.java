package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;

/** A client for receiving network communications from a server.
 * Uses {@link ClientDiscoveryRunnable} to find a server.
 * In the context of the game, the server is run by the player, and the clients are beasts controlled by other players. */
public class Client extends NetworkInterface {

    private static final int SERVER_WAIT_TIME = 1000;

    private final com.esotericsoftware.kryonet.Client kryoClient = new com.esotericsoftware.kryonet.Client();

    @Override
    public boolean start() {
        InetAddress address = searchServer();
        if (address == null) {
            Gdx.app.log("Client", "No servers found.");
            return false;
        }

        kryoClient.start();
        registerClasses(kryoClient.getKryo());

        try {
            kryoClient.connect(3000, address, PORT);
            setupListeners();
        } catch (IOException e) {
            Gdx.app.error("Client", "Couldn't connect to server at address " + address.getHostName() + "!");
            return false;
        }
        return true;
    }

    // Search a server; connect if one is found, return false if not
    private static InetAddress searchServer() {
        ClientDiscoveryRunnable searchRunnable = new ClientDiscoveryRunnable();
        Thread serverSearch = new Thread(searchRunnable);
        serverSearch.start();
        try {
            serverSearch.join(SERVER_WAIT_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return searchRunnable.getAddress();
    }

    private void setupListeners() {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                // TODO Handle receiving packets
            }
        });
    }

    @Override
    public void send(Object toSend) {
        kryoClient.sendTCP(toSend);
    }

    @Override
    public void dispose() {
        kryoClient.close();
    }
}