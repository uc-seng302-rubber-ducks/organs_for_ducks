package odms.commons.model._enum;

public enum AppointmentStatus {
    PENDING("Pending", 1),
    ACCEPTED("Accepted", 2),
    REJECTED("Rejected", 3),
    CANCELED("Canceled", 4),
    UPDATED("Updated", 5),
    ACCEPTED_SEEN("Accepted Seen", 6),
    REJECTED_SEEN("Rejected Seen", 7),
    CANCELLED_SEEN("Cancelled Seen", 8);

    private final String name;
    private final int dbValue;

    AppointmentStatus(String s, int i)  {
        name = s;
        dbValue = i;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getDbValue() {
        return this.dbValue;
    }
}
