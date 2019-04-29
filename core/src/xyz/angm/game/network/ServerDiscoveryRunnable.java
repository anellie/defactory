package xyz.angm.game.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** Waits for a {@link ClientDiscoveryRunnable} to ping it and gives a response for the client to find it. */
class ServerDiscoveryRunnable implements Runnable {

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(Client.PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setBroadcast(true);

            while (true) {
                // Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                // Check if the message is correct; send back a new packet
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_GAME_REQUEST")) {
                    byte[] sendData = "DISCOVER_GAME_RESPONSE".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    break;
                }
            }
        } catch (IOException ex) {
            Gdx.app.error("Server", "Could not initialize discovery thread. Exiting.");
            System.exit(-1);
        }
    }
}
