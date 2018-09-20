package odms.commons.model._enum;

public enum BloodTestProperties {
    RBC("Red Blood Cell", 1),
    WBC("White Blood Cell", 2),
    HAEMOGLOBIN("Haemoglobin", 3),
    PLATELETS("Platelets", 4),
    GLUCOSE("Glucose", 5),
    HAEMATOCRIT("Haematocrit", 6),
    MEAN_CELL_VOLUME("Mean Cell Volume", 7),
    MEAN_CELL_HAEMATOCRIT("meanCellHaematocrit", 8);

    private final String name;
    private final int dbValue;

    BloodTestProperties(String s, int i)  {
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
