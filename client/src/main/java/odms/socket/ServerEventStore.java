package odms.socket;

import javafx.application.Platform;
import odms.commons.model._abstract.Listenable;
import odms.commons.model.dto.UpdateNotification;
import odms.commons.model.event.UpdateNotificationEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * class to store incoming messages from the server so they can be read by other classes within the client
 */
public class ServerEventStore implements Listenable {

    private static ServerEventStore eventStore;
    private PropertyChangeSupport pcs;

    private ServerEventStore() {
        this.pcs = new PropertyChangeSupport(this);
    }

    public static ServerEventStore getInstance() {
        if (eventStore == null) {
            eventStore = new ServerEventStore();
        }
        return eventStore;
    }

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

    @Override
    public void fire(PropertyChangeEvent event) {
        Platform.runLater(() -> pcs.firePropertyChange(event));
    }
}
