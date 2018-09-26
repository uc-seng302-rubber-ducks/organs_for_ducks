package odms.commons.model._enum;

public enum EventTypes {
    USER_UPDATE("USER"),
    CLINICIAN_UPDATE("CLINICIAN"),
    ADMIN_UPDATE("ADMIN"),
    COUNTRY_UPDATE("COUNTRY"),
    APPOINTMENT_UPDATE("APPOINTMENT"),
    REQUEST_UPDATE("REQUEST"),
    BLOOD_TEST_UPDATE("BLOOD_TEST");

    private final String name;

    EventTypes(String name) {
        this.name = name;
    }
}


