package odms.socket;

import com.google.gson.Gson;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.UpdateNotification;
import odms.commons.utils.Log;
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

    /**
     * for testing only
     *
     * @return current sessions
     */
    public static List<WebSocketSession> getSessions() {
        return sessions;
    }

    /**
     * for testing
     *
     * @param sessions1 desired list of (mock) sessions
     */
    public static void setSessions(List<WebSocketSession> sessions1) {
        sessions = sessions1;
    }

    /**
     * The server should never receive any messages within the intended use of the app.
     * This method exists only provide some ping/pong connection testing
     *
     * @param session session from which a message was received
     * @param message message that was received
     * @throws IOException if unable to respond to the message (i.e. connection error)
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        session.sendMessage(new TextMessage("hello world"));
    }

    /**
     * adds the newly connected session to a list of existing sessions that will be used to broadcast messages
     *
     * @param session new session that has been established
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        Log.info("added new session");
    }

    /**
     * sends a message to all connected sessions. if a connection is encountered that is no longer active, it will be removed from the list
     *
     * @param type          the role of user that has been changed
     * @param newIdentifier the new nhi/staff id/username of the user. this may be the same as the old identifier
     * @param oldIdentifier the old nhi/staff id/username of the user.
     * @throws IOException exception thrown if the message cannot be sent or serialised
     */
    public void broadcast(EventTypes type, String newIdentifier, String oldIdentifier) throws IOException {

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                UpdateNotification updateNotification = new UpdateNotification(type, oldIdentifier, newIdentifier);
                session.sendMessage(new TextMessage(new Gson().toJson(updateNotification)));
                Log.info("broadcasted update message about id " + newIdentifier + "/" + oldIdentifier);
            }
        }
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
                Log.info("pruned dead session");
            }
        }
    }
}
