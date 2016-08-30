package io.faucette.messenger;


import static org.junit.Assert.*;
import org.junit.*;


import org.json.JSONObject;


public class MessengerTest {
    @Test public void testMessenger() {
        ServerClient serverClient = SimpleAdapter.createServerClient();

        Messenger serverMessenger = new Messenger(serverClient.server);
        Messenger clientMessenger = new Messenger(serverClient.client);

        Callback callback = new Callback() {
            @Override
            public void call(JSONObject data, Callback next) {
                assertEquals(data.toString(), "{\"data\":\"data\"}");
                next.call(null, (JSONObject) null);
            }
        };

        clientMessenger.on("test", callback);
        serverMessenger.send("test", new JSONObject("{\"data\": \"data\"}"));
        clientMessenger.off("test", callback);
    }
}
