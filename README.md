io.faucette.messenger
=======

simple socket like messenger for java

```java
import io.faucette.messenger.Messenger;
import some.adapter.Adapter;

/*
public interface Adapter {
    public void addMessageListener(Callback callback);
    public void onMessage(String data);
    public void postMessage(String data);
}
*/


Messenger messenger = new Messenger(new Adapter());

// callback optional
messenger.emit("message", new JSONObject("{\"data\": \"data\"}"), new Callback() {
    @Override
    public void call(JSONObject error, JSONObject data) {
        // check if error is not null and do something with json data
    }
});
```