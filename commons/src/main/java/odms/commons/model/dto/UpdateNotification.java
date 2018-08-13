package odms.commons.model.dto;

import odms.commons.model._enum.EventTypes;

public class UpdateNotification {
    private EventTypes eventType;
    private String oldId;
    private String newId;

    public UpdateNotification(EventTypes eventType, String oldId, String newId) {

        this.eventType = eventType;
        this.oldId = oldId;
        this.newId = newId;
    }

    public EventTypes getEventType() {
        return eventType;
    }

    public void setEventType(EventTypes eventType) {
        this.eventType = eventType;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }
}
