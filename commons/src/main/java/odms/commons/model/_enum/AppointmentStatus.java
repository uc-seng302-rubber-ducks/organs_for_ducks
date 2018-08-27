package odms.commons.model._enum;

public enum AppointmentStatus {
    PENDING("Pending", 1),
    ACCEPTED("Accepted", 2),
    REJECTED("Rejected", 3),
    CANCELED("Canceled", 4);

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
