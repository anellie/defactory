package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Consumer;

/** A client for receiving network communications from a server.
 * Uses {@link ClientDiscoveryRunnable} to find a server.
 * In the context of the game, the server is run by the player, and the clients are beasts controlled by spectators. */
public class Client extends NetworkInterface {

    /** The amount of time the client will wait for a server to respond to a discovery call, in ms. */
    private static final int SERVER_WAIT_TIME = 1000;

    private final com.esotericsoftware.kryonet.Client kryoClient = new com.esotericsoftware.kryonet.Client();

    @Override
    public boolean start() {
        InetAddress address = searchServer();
        if (address == null) {
            Gdx.app.log("Client", "No servers found.");
            return false;
        }

        try {
            kryoClient.start();
            registerClasses(kryoClient.getKryo());
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

    /** Add a listener to be called when an object is received from the server.
     * Will also be called on disconnect; the object will be a Status.DISCONNECTED.
     * @param listener The consumer to be called. */
    public void addListener(Consumer<Object> listener) {
        kryoClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                listener.accept(object);
            }

            @Override
            public void disconnected(Connection connection) {
                listener.accept(Status.DISCONNECTED);
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

    /** Used for giving status messages to listeners. */
    public enum Status {
        /** Used when kryoClient fires an disconnect event. */
        DISCONNECTED,
        /** Start of a beast wave. */
        WAVE_START,
        /** End of a beast wave. */
        WAVE_END
    }
}