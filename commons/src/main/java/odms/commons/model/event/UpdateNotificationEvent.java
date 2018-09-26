package odms.commons.model.event;

import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.UpdateNotification;

import java.beans.PropertyChangeEvent;

public class UpdateNotificationEvent extends PropertyChangeEvent {
    private String oldIdentifier;
    private String newIdentifier;
    private EventTypes type;

    /**
     * Constructs a new {@code PropertyChangeEvent}.
     *
     * @param source       the bean that fired the event
     * @param propertyName the programmatic name of the property that was changed
     * @param oldValue     the old value of the property
     * @param newValue     the new value of the property
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public UpdateNotificationEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }

    /**
     * Creates an event based on a notification. note that the default oldValue and newValue are set to a constant
     * as these must be different for the event to fire properly.
     *
     * @param source       the bean that fired the event
     * @param notification object containing old and new id's, and event type
     */
    public UpdateNotificationEvent(Object source, UpdateNotification notification) {
        super(source, "", null, "not null");
        this.newIdentifier = notification.getNewId();
        this.oldIdentifier = notification.getOldId();
        this.type = notification.getEventType();
    }

    public String getOldIdentifier() {
        return oldIdentifier;
    }

    public String getNewIdentifier() {
        return newIdentifier;
    }

    public EventTypes getType() {
        return type;
    }
}
