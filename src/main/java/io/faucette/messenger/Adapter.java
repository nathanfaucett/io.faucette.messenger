package io.faucette.messenger;


public interface Adapter {
    public void addMessageListener(Callback callback);
    public void onMessage(String data);
    public void postMessage(String data);
}
