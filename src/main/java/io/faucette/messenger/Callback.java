package io.faucette.messenger;


import org.json.JSONObject;


public class Callback {
    public void call(JSONObject data) {}
    public void call(JSONObject error, JSONObject data) {}
    public void call(JSONObject data, Callback callback) {}
}
