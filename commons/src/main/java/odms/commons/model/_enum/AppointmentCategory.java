package odms.commons.model._enum;

public enum AppointmentCategory {
    BLOOD_TEST("Blood test", 1),
    GENERAL_CHECK_UP("General check up", 2),
    HEALTH_ADVICE("Health advice", 3),
    PRESCRIPTION_RENEWAL("Prescription renewal", 4),
    OTHER("Other", 5);

    private final String name;
    private final int dbValue;

    AppointmentCategory(String s, int i)  {
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
