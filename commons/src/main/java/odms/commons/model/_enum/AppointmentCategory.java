package odms.commons.model._enum;

public enum AppointmentCategory {
    BLOOD_TEST("Blood test"),
    GENERAL_CHECK_UP("General check up"),
    INJECTION("Injection"),
    PRESCRIPTION_RENEWAL("Prescription renewal");

    private final String name;

    AppointmentCategory(String s)  {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
