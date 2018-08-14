package odms.socket;

import com.google.gson.Gson;
import odms.commons.model.dto.UpdateNotification;
import odms.commons.utils.Log;
import okhttp3.*;

import java.beans.PropertyChangeSupport;


public class OdmsSocketHandler {

    private PropertyChangeSupport pcs;
    private OkHttpClient client;
    private WebSocket socket;
    private ServerEventNotifier eventStore;
    private int numRetries = 0;
    private String url = "";
    private WebSocketListener listener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.info("websocket to server has been opened on port " + response.request().url().port());
            numRetries = 0;

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            UpdateNotification updateNotification = new Gson().fromJson(text, UpdateNotification.class);
            eventStore.fire(updateNotification);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.warning("closing websocket. code: " + code + ", reason: " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.warning("websocket is closed. code: " + code + ", reason: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.severe("failed to open websocket with server", t);
            try {
                retry();
            } catch (InterruptedException e) {
                Log.severe("failed to retry connection, cancelling", e);
                Thread.currentThread().interrupt();
            }
        }

    };

    public OdmsSocketHandler(OkHttpClient client, ServerEventNotifier eventStore) {
        this.pcs = new PropertyChangeSupport(this);
        this.client = client;
        this.eventStore = eventStore;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebSocketListener getListener() {
        return listener;
    }

    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }

    /**
     * attempts to make a websocket with the given url.
     *
     * @param url correctly formatted url (ws://your.address/here)
     */
    public void start(String url) {
        Request request = new Request.Builder().url(url).build();
        socket = client.newWebSocket(request, listener);
        this.url = url;
    }

    /**
     * closes the socket with a 1000 (normal closure) code (if it exists)
     */
    public void stop() {
        if (socket != null) {
            socket.close(1000, "socket closed by client");
            Log.info("websocket manually closed by client");
        }
    }


    /**
     * attempts to run the start method, incrementing a counter each time it is called.
     * if the counter exceeds a maximum, it will log a warning and cease
     *
     * @throws InterruptedException if the wait time between attempts is interrupted
     */
    public void retry() throws InterruptedException {
        int maxRetries = 5;
        int retryBackoffMs = 3000;

        if (numRetries < maxRetries) {
            numRetries++;
            Thread.sleep(retryBackoffMs);
            start(url);
        }
        Log.warning("unable to connect websocket after " + numRetries);
    }
}
