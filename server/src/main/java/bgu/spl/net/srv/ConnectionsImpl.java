package bgu.spl.net.srv;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public class ConnectionsImpl<T> implements Connections <T > {
    private final WeakHashMap<Integer, ConnectionHandler<T>> connections = new WeakHashMap<>();

    @Override
    public void connect(int connectionId, ConnectionHandler<T> handler) {
        connections.put(connectionId, handler);
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(connections.containsKey(connectionId)){
            connections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }
    
    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }

    @Override
    public void lock(int connectionId) {
        connections.get(connectionId).lock();
    }

    @Override
    public void unlock(int connectionId) {
        connections.get(connectionId).unlock();
    }

    @Override
    public boolean isLoggedIn(int connectionId) {
        return connections.get(connectionId).isLoggedIn();
    }

    @Override
    public void login(int connectionId, String username) {
        connections.get(connectionId).login(username);
    }

    @Override
    public Set<Integer> getKeys() {
        return connections.keySet();
    }

    
}
