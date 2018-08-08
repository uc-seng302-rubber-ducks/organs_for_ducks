package odms.socket;

import javafx.application.Platform;
import odms.commons.model._abstract.Listenable;
import odms.commons.model.dto.UpdateNotification;
import odms.commons.model.event.UpdateNotificationEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * class to store incoming messages from the server so they can be read by other classes within the client
 */
public class ServerEventNotifier implements Listenable {

    private static ServerEventNotifier eventNotifier;
    private PropertyChangeSupport pcs;

    private ServerEventNotifier() {
        this.pcs = new PropertyChangeSupport(this);
    }

    public static ServerEventNotifier getInstance() {
        if (eventNotifier == null) {
            eventNotifier = new ServerEventNotifier();
        }
        return eventNotifier;
    }

    /**
     * calls the fire method after creating an UpdateNotificationEvent for the given notification. use this in preference to the overridden method
     *
     * @param notification update notification to be sent to listeners
     * @see UpdateNotificationEvent
     */
    public void fire(UpdateNotification notification) {
        fire(new UpdateNotificationEvent(this, notification));
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * fires an event to all listeners on the main/javafx thread
     *
     * @param event PropertyChangeEvent to be fired
     */
    @Override
    public void fire(PropertyChangeEvent event) {
        Platform.runLater(() -> pcs.firePropertyChange(event));
    }
}
