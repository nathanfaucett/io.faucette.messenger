package io.faucette.messenger;


import org.json.JSONObject;


public class SimpleAdapter implements Adapter {
    public SimpleAdapter socket;
    public Callback message;

    public SimpleAdapter() {
        socket = null;
        message = null;
    }

    public void addMessageListener(Callback callback) {
        message = callback;
    }
    public void onMessage(JSONObject data) {
        message.call(data);
    }
    public void postMessage(JSONObject data) {
        socket.onMessage(data);
    }

    public static ServerClient createServerClient() {
        SimpleAdapter client = new SimpleAdapter();
        SimpleAdapter server = new SimpleAdapter();

        client.socket = server;
        server.socket = client;

        return new ServerClient(server, client);
    }
}
