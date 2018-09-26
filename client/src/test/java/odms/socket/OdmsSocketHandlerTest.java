package odms.socket;

import com.google.gson.Gson;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.UpdateNotification;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class OdmsSocketHandlerTest {

    private final String testUrl = "http://url.com"; //Request has regex/checking for format
    private OdmsSocketHandler handler;
    private WebSocketListener listener;
    private OkHttpClient client;
    private ServerEventNotifier eventStore;

    @Before
    public void setUp() {
        client = mock(OkHttpClient.class);
        eventStore = mock(ServerEventNotifier.class);
        handler = new OdmsSocketHandler(client, eventStore);
        listener = handler.getListener();
    }

    @Test
    public void startShouldTryOpenNewSocket() {
        handler.start(testUrl);
        verify(client, times(1)).newWebSocket(any(Request.class), any(WebSocketListener.class));
    }


    @Test
    public void listenerShouldCallEventStoreOnMessage() {
        WebSocket mockSocket = mock(WebSocket.class);
        UpdateNotification notification = new UpdateNotification(EventTypes.USER_UPDATE, "ABC1234", "ABC1235");

        listener.onMessage(mockSocket, new Gson().toJson(notification));

        verify(eventStore, times(1)).fire(any(UpdateNotification.class));
    }

//    @Test
//    public void listenerShouldRetryOnFailure() throws InterruptedException {
//        OdmsSocketHandler spyHandler = spy(handler);
//        handler.setUrl(testUrl);
//        doNothing().when(spyHandler).start(isA(String.class));
//        spyHandler.getListener().onFailure(null, null, null);
//        verify(spyHandler).retry();
//        Assert.fail("not yet implemented");
//    }

    @Test
    public void stopShouldCloseSocketWithCode1000() {
        WebSocket mockSocket = mock(WebSocket.class);
        when(client.newWebSocket(any(Request.class), any(WebSocketListener.class))).thenReturn(mockSocket);

        handler.start(testUrl);

        handler.stop();
        verify(mockSocket).close(eq(1000), any(String.class));
    }

    @Test
    public void stopShouldNotFailOnNullSocket() {
        when(client.newWebSocket(any(Request.class), any(WebSocketListener.class))).thenReturn(null);
        handler.start(testUrl);

        //test to see if this line throws any exceptions
        handler.stop();
    }
}