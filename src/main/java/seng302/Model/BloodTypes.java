package seng302.Model;

public enum BloodTypes {
    AMINUS ("A-"),
    APLUS ("A+"),
    BPLUS ("B+"),
    BMINUS ("B-"),
    OMINUS ("O-"),
    OPLUS ("O+"),
    ABMINUS ("AB+"),
    ABPLUS ("AB-");

    private final String name;

    private BloodTypes(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
