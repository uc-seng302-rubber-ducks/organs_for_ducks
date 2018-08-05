package odms.socket;

import com.google.gson.Gson;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.UpdateNotification;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class OdmsSocketHandlerTest {

    private OdmsSocketHandler handler;
    private WebSocketListener listener;
    private OkHttpClient client;
    private ServerEventStore eventStore;
    private final String testUrl = "http://url.com"; //Request has regex/checking for format

    @Before
    public void setUp() {
        client = mock(OkHttpClient.class);
        eventStore = mock(ServerEventStore.class);
        handler = new OdmsSocketHandler(client, eventStore);
        listener = handler.getListener();
    }
    @Test
    public void startShouldTryOpenNewSocket() {
        handler.start(testUrl);
        verify(client, times(1)).newWebSocket(any(Request.class), any(WebSocketListener.class));
    }


    @Test
    public void listenerShouldFireEventOnMessage() {
        WebSocket mockSocket = mock(WebSocket.class);
        UpdateNotification notification = new UpdateNotification(EventTypes.USER_UPDATE, "ABC1234", "ABC1235");
        TestEventListener testEventListener = new TestEventListener();
        handler.addPropertyChangeListener(testEventListener);

        listener.onMessage(mockSocket, new Gson().toJson(notification));

        Assert.assertFalse(testEventListener.events.isEmpty());
        PropertyChangeEvent event = testEventListener.events.get(0);
        Assert.assertEquals(EventTypes.USER_UPDATE.name(), event.getPropertyName());
        Assert.assertEquals("ABC1234", event.getOldValue());
        Assert.assertEquals("ABC1235", event.getNewValue());
    }


    /**
     * very simple class to catch events fired by the web socket listener
     */
    private class TestEventListener implements PropertyChangeListener {
        private List<PropertyChangeEvent> events = new ArrayList<>();

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            events.add(evt);
        }
    }
}