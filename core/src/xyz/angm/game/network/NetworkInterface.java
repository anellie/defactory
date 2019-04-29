package xyz.angm.game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public abstract class NetworkInterface {

    static final int PORT = 35953;

    final Listener listener = new Listener() {
        @Override
        public void received(Connection connection, Object object) {
            // TODO Handle receiving packets
        }

        @Override
        public void disconnected(Connection connection) {
            // TODO Handle disconnect
        }
    };

    void registerClasses(Kryo kryo) {
    }

    public boolean connect() {
        throw new UnsupportedOperationException("This Interface cannot connect.");
    }

    public void start() {
        throw new UnsupportedOperationException("This Interface cannot be started.");
    }

    public abstract void disconnect();

    public abstract void send(Object toSend);
}
