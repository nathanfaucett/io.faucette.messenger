package io.faucette.messenger;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class Messenger {
    private String _id;
    private int _messageId;
    private Adapter _adapter;
    private HashMap<String, Callback> _callbacks;
    private HashMap<String, ArrayList<Callback>> _listeners;


    public Messenger(Adapter adapter) {
        final Messenger _this = this;

        _id = UUID.randomUUID().toString();
        _messageId = 0;
        _callbacks = new HashMap<String, Callback>();
        _listeners = new HashMap<String, ArrayList<Callback>>();

        _adapter = adapter;

        adapter.addMessageListener(new Callback() {
            @Override
            public void call(String data) {
                _this.onMessage(data);
            }
        });
    }

    public void onMessage(String data) throws JSONException {
        JSONObject message = new JSONObject(data);
        String messageId = message.getString("id");

        if (!message.isNull("name")) {
            String name = message.getString("name");

            if (_listeners.containsKey(name)) {
                final String id = messageId;
                final Adapter adapter = _adapter;

                JSONException error = _send(message.getJSONObject("data"), _listeners.get(name));
                adapter.postMessage(
                    "{" +
                        "\"id\": \""+ id + "\"," +
                        "\"error\": "+ (error != null ? ("\"" + error.getMessage() + "\"") : "null") + "," +
                        "\"data\": "+ (data != null ? data.toString() : "null") +
                    "}"
                );
            }
        } else {
            if (isMatch(messageId, _id) && _callbacks.containsKey(messageId)) {
                Callback callback = _callbacks.get(messageId);
                _callbacks.remove(messageId);

                JSONException error = null;
                if (!message.isNull("error")) {
                    error = new JSONException(message.getString("error"));
                }

                callback.call(error, message.getJSONObject("data"));
            }
        }
    }

    public void send(String name, JSONObject data, Callback callback) throws JSONException {
        String id = _id + "." + Integer.toString(_messageId++, 36);

        if (callback != null && !_callbacks.containsKey(id)) {
            _callbacks.put(id, callback);
        }

        _adapter.postMessage(
            "{" +
                "\"id\": \""+ id +"\"," +
                "\"name\": \""+ name +"\"," +
                "\"data\": "+ (data != null ? data.toString() : "null") +
            "}"
        );
    }
    public void send(String name, JSONObject data) throws JSONException {
        String id = _id + "." + Integer.toString(_messageId++, 36);

        _adapter.postMessage(
            "{" +
                "\"id\": \""+ id +"\"," +
                "\"name\": \""+ name +"\"," +
                "\"data\": "+ (data != null ? data.toString() : "null") +
            "}"
        );
    }

    public void on(String name, Callback callback) {
        if (callback != null) {
            ArrayList<Callback> listenerCallbacks;

            if (_listeners.containsKey(name) == false) {
                listenerCallbacks = new ArrayList<Callback>();
                _listeners.put(name, listenerCallbacks);
            } else {
                listenerCallbacks = _listeners.get(name);
            }

            listenerCallbacks.add(callback);
        }
    }
    public void off(String name, Callback callback) {
        if (callback != null) {
            if (_listeners.containsKey(name) != false) {
                ArrayList<Callback> listenerCallbacks = _listeners.get(name);
                int i = listenerCallbacks.size();

                while (i-- != 0) {
                    if (listenerCallbacks.get(i) == callback) {
                        listenerCallbacks.remove(i);
                    }
                }
            }
        }
    }

    private static JSONException _send(JSONObject data, ArrayList<Callback> listenerCallbacks) {
        JSONException error = null;

        for (Callback callback: listenerCallbacks) {
            try {
                callback.call(data);
            } catch(Exception ex) {
                error = new JSONException(ex.getMessage());
                break;
            }
        }

        return error;
    }

    private static boolean isMatch(String messageId, String messengerId) {
        return messageId.split("\\.")[0].equals(messengerId);
    }
}
