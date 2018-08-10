package odms.commons.model._enum;

/**
 * Enum constructor for Alcohol Level
 */
public enum AlcoholLevel {
    NONE("None"),
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High");

    private final String name;

    AlcoholLevel(String s) {
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
