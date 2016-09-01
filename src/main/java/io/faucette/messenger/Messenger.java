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
        String id = message.getString("id");

        if (!message.isNull("name")) {
            String name = (String) message.get("name");

            if (_listeners.containsKey(name)) {
                final String finalId = id;
                final Adapter adapter = _adapter;

                _send((JSONObject) message.get("data"), _listeners.get(name), new Callback() {
                    @Override
                    public void call(JSONObject error, JSONObject data) {
                        adapter.postMessage(
                            "{" +
                                "\"id\": \""+ finalId + "\"," +
                                "\"error\": "+ (error != null ? error.toString() : "null") + "," +
                                "\"data\": "+ (data != null ? data.toString() : "null") +
                            "}"
                        );
                    }
                });
            }
        } else {
            if (isMatch(id, _id) && _callbacks.containsKey(id)) {
                Callback callback = _callbacks.get(id);
                _callbacks.remove(id);
                callback.call((JSONObject) message.get("data"));
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

    private class Index {
        public int value;

        public Index() {
            value = 0;
        }
    }

    private void _send(JSONObject data, ArrayList<Callback> listenerCallbacks, Callback callback) {
        final Index index = new Index();
        final ArrayList<Callback> finalListenerCallbacks = listenerCallbacks;
        final Callback finalCallback = callback;

        Callback next = new Callback() {
            @Override
            public void call(JSONObject error, JSONObject data) {
                if (error != null || index.value == finalListenerCallbacks.size()) {
                    finalCallback.call(error, data);
                } else {
                    int i = index.value;
                    index.value++;
                    finalListenerCallbacks.get(i).call(data, this);
                }
            }
        };

        next.call(null, data);
    }

    private boolean isMatch(String messageId, String id) {
        return messageId.split("\\.")[0] == id;
    }
}
