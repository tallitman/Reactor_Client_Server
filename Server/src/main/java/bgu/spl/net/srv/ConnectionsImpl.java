package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> fConnections;

    public ConnectionsImpl() {
        this.fConnections = new ConcurrentHashMap<>();
    }

    @Override

    public boolean send(int connectionId, T msg) {

        ConnectionHandler<T> connectionHandler = this.fConnections.get(new Integer(connectionId));
        if (connectionHandler != null) {
            connectionHandler.send(msg);
            return true;
        }
        return false;
    }

    public void addConnection(int id, ConnectionHandler<T> con) {
        this.fConnections.put(id, con);
    }

    @Override
    public void broadcast(T msg) {

        fConnections.values().forEach((connectionHandler) -> connectionHandler.send(msg));

    }

    @Override
    public void disconnect(int connectionId) {

        this.fConnections.remove(new Integer(connectionId));
    }
}
