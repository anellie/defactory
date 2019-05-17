package xyz.angm.game.network;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import xyz.angm.game.world.TileVector;
import xyz.angm.game.world.blocks.Block;
import xyz.angm.game.world.entities.Beast;
import xyz.angm.game.world.entities.Player;

/** A simple network interface for sending and receiving packets. */
public abstract class NetworkInterface {

    /** The port all network communication takes place on. */
    static final int PORT = 35953;

    /** Registers all required classes for network communication to a kryo serializer.
     * @param kryo The kryo instance to register on */
    void registerClasses(Kryo kryo) {
        kryo.register(Player.class);
        kryo.register(Beast.class);
        kryo.register(Block.class);
        kryo.register(Block.Direction.class);
        kryo.register(TileVector.class);

        kryo.register(Vector2.class);
        kryo.register(Array.class);
        kryo.register(Object[].class);
    }

    /** Will start the interface along with a discovery thread.
     * @return Success of starting the interface and establishing a connection. */
    public abstract boolean start();

    /** Sends an object to either the server, or all clients.
     * @param toSend The object to be sent over network. */
    public abstract void send(Object toSend);

    /** Should be called when the interface should disconnect/close and free all resources. */
    public abstract void dispose();
}
