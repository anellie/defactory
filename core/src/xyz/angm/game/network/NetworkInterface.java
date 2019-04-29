package xyz.angm.game.network;

import com.esotericsoftware.kryo.Kryo;

/** A simple network interface for sending and receiving packets.
 * TODO Asses if this interface makes sense, or Client/Server should be fully separated. */
public abstract class NetworkInterface {

    /** The port all network communication takes place on. */
    static final int PORT = 35953;

    /** Registers all required classes for network communication.
     * @param kryo The kryo instance to register on */
    void registerClasses(Kryo kryo) {
    }

    /** Will start the interface along with a discovery thread.
     * @return Success of starting the interface and establishing a connection. */
    public abstract boolean start();

    /** Sends an object to either the server, or all clients.
     * @param toSend The object to be sent over network */
    public abstract void send(Object toSend);

    /** Should be called when the interface should disconnect/close and free all resources. */
    public abstract void dispose();
}
