package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.InetAddress;

/** A client for receiving network communications from a server.
 * Uses {@link ClientDiscoveryRunnable} to find a server.
 * In the context of the game, the server is run by the player, and the clients are beasts controlled by other players. */
public class Client extends NetworkInterface {

    private static final int SERVER_WAIT_TIME = 1000;

    private com.esotericsoftware.kryonet.Client kryoClient;

    /** Searches and connects to a server. Can block the thread for up to {@value SERVER_WAIT_TIME}ms,
     * so it should run in a separate thread.
     * @return If a server was found and connected to */
    public boolean connect() {
        InetAddress address = searchServer();
        if (address == null) {
            Gdx.app.log("Client", "No servers found.");
            return false;
        }

        kryoClient = new com.esotericsoftware.kryonet.Client();
        kryoClient.start();
        registerClasses(kryoClient.getKryo());

        try {
            kryoClient.connect(3000, address, PORT);
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

    @Override
    public void send(Object toSend) {
        kryoClient.sendTCP(toSend);
    }

    @Override
    public void dispose() {
        kryoClient.close();
    }
}