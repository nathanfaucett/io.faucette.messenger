package io.faucette.messenger;


public class ServerClient {
    public SimpleAdapter server;
    public SimpleAdapter client;

    public ServerClient(SimpleAdapter s, SimpleAdapter c) {
        server = s;
        client = c;
    }
}
