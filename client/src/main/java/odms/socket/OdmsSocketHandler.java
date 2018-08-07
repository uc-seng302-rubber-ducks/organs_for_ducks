package odms.socket;

import com.google.gson.Gson;
import odms.commons.exception.ConnectionException;
import odms.commons.model._abstract.Listenable;
import odms.commons.model.dto.UpdateNotification;
import odms.commons.utils.Log;
import okhttp3.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class OdmsSocketHandler implements Listenable {

    private PropertyChangeSupport pcs;
    private OkHttpClient client;
    private WebSocket socket;
    private ServerEventStore eventStore;
    private final int MAX_RETRIES = 5;
    private final int RETRY_BACKOFF_MS = 3000;
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

    public OdmsSocketHandler(OkHttpClient client, ServerEventStore eventStore) {
        this.pcs = new PropertyChangeSupport(this);
        this.client = client;
        this.eventStore = eventStore;
    }

    public WebSocketListener getListener() {
        return listener;
    }

    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }

    /**
     * attempts to make a websocket with the given url. may throw a
     * @see ConnectionException on error
     * @param url correctly formatted url (ws://your.address/here)
     */
    public void start(String url) {
        Request request = new Request.Builder().url(url).build();
        socket = client.newWebSocket(request, listener);
        this.url = url;
    }

    public void stop() {
        if (socket != null) {
            socket.close(1000, "socket closed by client");
            Log.info("websocket manually closed by client");
        }
    }

    public void retry() throws InterruptedException {
        if (numRetries < MAX_RETRIES) {
            numRetries++;
            Thread.sleep(RETRY_BACKOFF_MS);
            start(url);
        }
        Log.warning("unable to connect websocket after " + numRetries);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void fire(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }
}
