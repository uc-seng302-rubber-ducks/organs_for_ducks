package odms.socket;

import odms.commons.model._abstract.Listenable;
import odms.commons.utils.Log;
import okhttp3.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class OdmsSocketHandler implements Listenable{

    private PropertyChangeSupport pcs;
    private OkHttpClient client;
    private WebSocket socket;

    public OdmsSocketHandler(OkHttpClient client) {
        this.pcs = new PropertyChangeSupport(this);
        this.client = client;
    }

    public void start(String url) {
        Request request = new Request.Builder().url(url).build();
        socket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.info("websocket to server has been opened on port" + response.request().url().port());
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                //TODO change this to be more intelligent
                fire(new PropertyChangeEvent(this, text, null, null));
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
            }
        });
    }

    public void stop() {
        if(socket != null) {
            socket.close(0, "socket closed by client");
            Log.info("websocket manually closed by client");
        }
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
