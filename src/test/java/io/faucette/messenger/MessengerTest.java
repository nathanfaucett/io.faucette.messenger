package io.faucette.messenger;


import static org.junit.Assert.*;
import org.junit.*;


import org.json.JSONObject;


public class MessengerTest {

    private class Index {
        public int value;

        public Index() {
            value = 0;
        }
    }

    @Test
    public void testMessenger() {
        ServerClient serverClient = SimpleAdapter.createServerClient();

        Messenger serverMessenger = new Messenger(serverClient.server);
        Messenger clientMessenger = new Messenger(serverClient.client);

        final Index index = new Index();

        Callback onClientResponse = new Callback() {
            @Override
            public void call(JSONObject data, Callback next) {
                index.value += 1;
                assertEquals(data.toString(), "{\"data\":\"data\"}");
                next.call(null, data);
            }
        };
        serverMessenger.on("message", onClientResponse);
        clientMessenger.send("message", new JSONObject("{\"data\": \"data\"}"));


        Callback onServerResponse = new Callback() {
            @Override
            public void call(JSONObject data, Callback next) {
                index.value += 1;
                assertEquals(data.toString(), "{\"data\":\"data\"}");
                next.call(null, data);
            }
        };
        clientMessenger.on("message", onServerResponse);
        serverMessenger.send("message", new JSONObject("{\"data\": \"data\"}"));

        assertEquals(index.value, 2);
    }
}
