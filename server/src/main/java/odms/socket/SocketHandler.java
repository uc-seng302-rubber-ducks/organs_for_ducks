package odms.socket;

import com.google.gson.Gson;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.UpdateNotification;
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

    public static List<WebSocketSession> getSessions() {
        return sessions;
    }

    public static void setSessions(List<WebSocketSession> sessions1) {
        sessions = sessions1;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage("Hello!"));
            } else {
                sessions.remove(webSocketSession);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    public void broadcast(EventTypes type, String newIdentifier, String oldIdentifier) throws IOException {

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                UpdateNotification updateNotification = new UpdateNotification(type, oldIdentifier, newIdentifier);
                session.sendMessage(new TextMessage(new Gson().toJson(updateNotification)));
            }
        }
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
            }
        }
    }
}
