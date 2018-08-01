package odms.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private static List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        for(WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage("Hello!"));
            } else {
                sessions.remove(webSocketSession);
            }
        }
    }

    public static List<WebSocketSession> getSessions() {
        return sessions;
    }

    public static void setSessions(List<WebSocketSession> sessions1) {
        sessions = sessions1;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    public void broadcast() throws IOException {
        for(WebSocketSession session: sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("beep boop"));
            }
        }
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
            }
        }
    }
}
