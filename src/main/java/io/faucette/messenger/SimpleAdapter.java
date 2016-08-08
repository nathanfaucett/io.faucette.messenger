package io.faucette.messenger;


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
    public void onMessage(String data) {
        message.call(data);
    }
    public void postMessage(String data) {
        socket.onMessage(data);
    }

    public static ServerClient createServerClient() {
        SimpleAdapter server = new SimpleAdapter();
        SimpleAdapter client = new SimpleAdapter();

        server.socket = client;
        client.socket = server;

        return new ServerClient(server, client);
    }
}
