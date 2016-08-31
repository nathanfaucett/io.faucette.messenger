package io.faucette.messenger;


import org.json.JSONObject;


public interface Adapter {
    public void addMessageListener(Callback callback);
    public void onMessage(JSONObject data);
    public void postMessage(JSONObject data);
}
