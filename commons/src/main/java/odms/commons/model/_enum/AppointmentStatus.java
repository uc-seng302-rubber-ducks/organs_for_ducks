package odms.commons.model._enum;

public enum AppointmentStatus {
    PENDING("Pending", 1),
    ACCEPTED("Accepted", 2),
    REJECTED("Rejected", 3),
    CANCELLED_BY_USER("Cancelled By User", 4),
    CANCELLED_BY_CLINICIAN("Cancelled By Clinician", 5),
    UPDATED("Updated", 6),
    ACCEPTED_SEEN("Accepted", 7),
    REJECTED_SEEN("Rejected", 8),
    CANCELLED_BY_USER_SEEN("Cancelled By User", 9),
    CANCELLED_BY_CLINICIAN_SEEN("Cancelled By Clinician", 10);

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
