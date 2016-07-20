package io.faucette.messenger;


import static org.junit.Assert.*;
import org.junit.*;


import org.json.JSONObject;


class TestAdapter implements Adapter {
    public TestAdapter socket;
    public Callback message;

    public TestAdapter() {
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
}


public class MessengerTest {
    @Test public void testMessenger() {
        TestAdapter server = new TestAdapter();
        TestAdapter client = new TestAdapter();

        server.socket = client;
        client.socket = server;

        Messenger serverMessenger = new Messenger(server);
        Messenger clientMessenger = new Messenger(client);

        Callback callback = new Callback() {
            @Override
            public void call(JSONObject data, Callback next) {
                assertEquals(data.toString(), "{\"data\":\"data\"}");
                next.call(null, (JSONObject) null);
            }
        };

        clientMessenger.on("test", callback);
        serverMessenger.emit("test", new JSONObject("{\"data\": \"data\"}"));
        clientMessenger.off("test", callback);
    }
}
