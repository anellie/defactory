package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.InetAddress;

public class Client extends NetworkInterface {

    private static final int SERVER_WAIT_TIME = 1000;

    private com.esotericsoftware.kryonet.Client kryoClient;

    // Search for a server and connect.
    // NOTE: This method can take SERVER_WAIT_TIME to execute while searching servers. Run it on a separate thread!
    @Override
    public boolean connect() {
        InetAddress address = searchServer();
        if (address == null) {
            Gdx.app.log("Client", "No servers found.");
            return false;
        }

        kryoClient = new com.esotericsoftware.kryonet.Client();
        kryoClient.start();
        kryoClient.addListener(listener);
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

    public void send(Object toSend) {
        kryoClient.sendTCP(toSend);
    }

    public void disconnect() {
        kryoClient.close();
    }
}