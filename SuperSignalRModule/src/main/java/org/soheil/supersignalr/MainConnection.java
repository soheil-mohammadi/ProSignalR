package org.soheil.supersignalr;

import android.net.Uri;
import org.soheil.supersignalr.hub.HubConnection;
import org.soheil.supersignalr.hub.HubConnectionListener;
import org.soheil.supersignalr.hub.HubEventListener;
import org.soheil.supersignalr.hub.HubMessage;
import org.soheil.supersignalr.socket.ConnectionSocket;
import org.soheil.supersignalr.socket.ConnectionSocketListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainConnection implements HubConnection, ConnectionSocketListener {

    private static String TAG = "MainConnection";

    private List<HubConnectionListener> listeners = new ArrayList<>();
    private Map<String, List<HubEventListener>> eventListeners = new HashMap<>();

    private ConnectionSocket connectionSocket;


    public MainConnection(String hubUrl, String authToken) {
        this.connectionSocket = new ConnectionSocket(Uri.parse(hubUrl) , "Bearer " + authToken
                , this);
    }

    @Override
    public synchronized void connect() {
        this.connectionSocket.connect();
    }

    private void error(Exception ex) {
        for (HubConnectionListener listener : listeners) {
            listener.onError(ex);
        }
    }

    @Override
    public void disconnect() {
        connectionSocket.disconnect();
    }

    @Override
    public synchronized boolean isConnected() {
        return connectionSocket.isConnected();
    }

    @Override
    public void addListener(HubConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HubConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void subscribeToEvent(String eventName, HubEventListener eventListener) {
        List<HubEventListener> eventMap;
        if (eventListeners.containsKey(eventName)) {
            eventMap = eventListeners.get(eventName);
        } else {
            eventMap = new ArrayList<>();
            eventListeners.put(eventName, eventMap);
        }
        eventMap.add(eventListener);
    }

    @Override
    public void unSubscribeFromEvent(String eventName, HubEventListener eventListener) {
        if (eventListeners.containsKey(eventName)) {
            List<HubEventListener> eventMap = eventListeners.get(eventName);
            eventMap.remove(eventListener);
            if (eventMap.isEmpty()) {
                eventListeners.remove(eventName);
            }
        }
    }

    @Override
    public void invoke(String event, Object... parameters) {
        connectionSocket.invoke(event , parameters);
    }

    @Override
    public void onOpened() {
        for (HubConnectionListener listener : listeners) {
            listener.onConnected();
        }
    }

    @Override
    public void onMessage(HubMessage hubMessage) {
//        for (HubConnectionListener listener : listeners) {
//            listener.onMessage(hubMessage);
//        }

        List<HubEventListener> hubEventListeners = eventListeners.get(hubMessage.getTarget());
        if (hubEventListeners != null) {
            for (HubEventListener listener : hubEventListeners) {
                listener.onEventMessage(hubMessage);
            }
        }
    }

    @Override
    public void onDisconnected() {
        for (HubConnectionListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    @Override
    public void onError(Exception e) {
        error(e);
    }
}