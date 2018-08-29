package odms.commons.model._enum;

public enum UserType {
    USER(0),
    CLINICIAN(1),
    ADMIN(2);

    private int value;

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
