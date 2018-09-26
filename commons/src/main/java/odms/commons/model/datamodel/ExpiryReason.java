package odms.commons.model.datamodel;

import java.time.LocalDateTime;

/**
 * Stores all info for organ expiry.
 */
public class ExpiryReason {
    private String id;
    private LocalDateTime timeOrganExpired;
    private String reason;
    private String name;

    public ExpiryReason(String id, LocalDateTime dateTime, String reason, String name) {
        this.id = id;
        this.timeOrganExpired = dateTime;
        this.reason = reason;
        this.name = name;
    }

    public ExpiryReason() {
        this.id = "";
        this.timeOrganExpired = null;
        this.reason = "";
        this.name = "";
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimeOrganExpired() {
        return timeOrganExpired;
    }

    public String getReason() {
        return reason;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeOrganExpired(LocalDateTime timeOrganExpired) {
        this.timeOrganExpired = timeOrganExpired;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExpiryReason{" +
                "id='" + id + '\'' +
                ", timeOrganExpired=" + timeOrganExpired +
                ", reason='" + reason + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
