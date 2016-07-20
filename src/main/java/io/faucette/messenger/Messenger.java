package io.faucette.messenger;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Messenger {
    private static int _MESSENGER_ID = 0;
    private int _id;
    private int _MESSAGE_ID = 0;
    private Adapter _adapter;
    private HashMap<String, Callback> _callbacks;
    private HashMap<String, ArrayList<Callback>> _listeners;


    public Messenger(Adapter adapter) {
        final Messenger _this = this;

        _id = Messenger._MESSENGER_ID++;
        _adapter = adapter;
        _callbacks = new HashMap<String, Callback>();
        _listeners = new HashMap<String, ArrayList<Callback>>();

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

        if (message.isNull("name")) {
            if (_callbacks.containsKey(id)) {
                _callbacks.get(id).call((JSONObject) message.get("data"));
                _callbacks.remove(id);
            }
        } else {
            String name = (String) message.get("name");

            if (_listeners.containsKey(name)) {
                final String finalId = id;
                final Adapter adapter = _adapter;

                _emit((JSONObject) message.get("data"), _listeners.get(name), new Callback() {
                    @Override
                    public void call(JSONObject error, JSONObject data) {
                        adapter.postMessage(
                            "{" +
                                "\"id\": "+ finalId + "," +
                                "\"error\": "+ (error != null ? error.toString() : "null") + "," +
                                "\"data\": "+ (data != null ? data.toString() : "null") +
                            "}"
                        );
                    }
                });
            }
        }
    }

    public void emit(String name, JSONObject data, Callback callback) throws JSONException {
        String id = Integer.toString(_id, 36) + "-" + Integer.toString(_MESSAGE_ID++, 36);

        if (callback != null) {
            _callbacks.put(id, callback);
        }

        _adapter.postMessage(
            "{" +
                "\"id\": "+ id +"," +
                "\"name\": \""+ name +"\"," +
                "\"data\": "+ (data != null ? data.toString() : "null") +
            "}"
        );
    }

    public void emit(String name, JSONObject data) throws JSONException {
        String id = Integer.toString(_id, 36) + "-" + Integer.toString(_MESSAGE_ID++, 36);

        _adapter.postMessage(
            "{" +
                "\"id\": "+ id +"," +
                "\"name\": \""+ name +"\"," +
                "\"data\": "+ (data != null ? data.toString() : "null") +
            "}"
        );
    }

    public void on(String name, Callback callback) {
        System.out.println(name);

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

    private void _emit(JSONObject data, ArrayList<Callback> listenerCallbacks, Callback callback) {
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
                    index.value += 1;
                    finalListenerCallbacks.get(i).call(data, this);
                }
            }
        };

        next.call(null, data);
    }
}
