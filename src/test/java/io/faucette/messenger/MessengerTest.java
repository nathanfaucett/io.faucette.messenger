package io.faucette.messenger;


import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import org.junit.*;

import org.json.JSONObject;


public class MessengerTest {


    public static ServerClient createServerClient() {
        SimpleAdapter client = new SimpleAdapter();
        SimpleAdapter server = new SimpleAdapter();

        client.socket = server;
        server.socket = client;

        return new ServerClient(server, client);
    }

    @Test
    public void testMessenger() {
        ServerClient serverClient = createServerClient();

        Messenger serverMessenger = new Messenger(serverClient.server);
        Messenger clientMessenger = new Messenger(serverClient.client);

        final AtomicInteger index = new AtomicInteger(0);

        Callback onClientResponse = new Callback() {
            @Override
            public void call(JSONObject data) {
                index.getAndIncrement();
                assertEquals(data.toString(), "{\"data\":\"data\"}");
            }
        };
        serverMessenger.on("message", onClientResponse);
        clientMessenger.send("message", new JSONObject("{\"data\": \"data\"}"));


        Callback onServerResponse = new Callback() {
            @Override
            public void call(JSONObject data) {
                index.getAndIncrement();
                assertEquals(data.toString(), "{\"data\":\"data\"}");
            }
        };
        clientMessenger.on("message", onServerResponse);
        serverMessenger.send("message", new JSONObject("{\"data\": \"data\"}"));

        assertEquals(index.get(), 2);
    }
}
