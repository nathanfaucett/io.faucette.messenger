package io.faucette.messenger;


import org.json.JSONObject;
import org.json.JSONException;


public class Callback {
    public void call(String data) {}
    public void call(JSONObject data) {}
    public void call(JSONException error, JSONObject data) {}
}
