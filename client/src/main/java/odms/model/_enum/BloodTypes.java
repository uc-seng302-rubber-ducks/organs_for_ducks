package odms.model._enum;

/**
 * Enum constructor for Blood Types
 */
public enum BloodTypes {
    AMINUS("A-"),
    APLUS("A+"),
    BPLUS("B+"),
    BMINUS("B-"),
    OMINUS("O-"),
    OPLUS("O+"),
    ABMINUS("AB+"),
    ABPLUS("AB-");

    private final String name;

    BloodTypes(String s) {
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
