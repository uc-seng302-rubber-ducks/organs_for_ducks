package odms.socket;

import odms.commons.model._enum.Environments;
import odms.commons.utils.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SocketHandlerTest {

    private SocketHandler handler;

    @Before
    public void setUp() {
        Log.setup(Environments.TEST);
        SocketHandler.getSessions().clear();
        handler = new SocketHandler();
    }

    @After
    public void tearDown() {
        SocketHandler.getSessions().clear();
    }

    @Test
    public void newConnectionAddsNewSessionToList() {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        handler.afterConnectionEstablished(mockSession);
        Assert.assertTrue(SocketHandler.getSessions().contains(mockSession));
    }

    @Test
    public void broadcastSendsMessageToAllActiveSessions() throws IOException {
        WebSocketSession mockSession1 = mock(WebSocketSession.class);
        when(mockSession1.isOpen()).thenReturn(true);
        WebSocketSession mockSession2 = mock(WebSocketSession.class);
        when(mockSession2.isOpen()).thenReturn(true);
        List<WebSocketSession> sessions = new ArrayList<>();
        sessions.add(mockSession1);
        sessions.add(mockSession2);

        SocketHandler.setSessions(sessions);
        handler.broadcast();

        verify(mockSession1, times(1)).sendMessage(any(WebSocketMessage.class));
        verify(mockSession2, times(1)).sendMessage(any(WebSocketMessage.class));
    }

    @Test
    public void broadcastRemovesClosedSessionsFromList() throws IOException {
        WebSocketSession mockSession1 = mock(WebSocketSession.class);
        when(mockSession1.isOpen()).thenReturn(false);

        WebSocketSession mockSession2 = mock(WebSocketSession.class);
        when(mockSession2.isOpen()).thenReturn(true);

        List<WebSocketSession> sessions = new ArrayList<>();
        sessions.add(mockSession1);
        sessions.add(mockSession2);

        SocketHandler.setSessions(sessions);
        handler.broadcast();

        Assert.assertFalse(SocketHandler.getSessions().contains(mockSession1));
        verify(mockSession2, times(1)).sendMessage(any(WebSocketMessage.class));
    }
}