package odms.commons.model._enum;

public enum BloodTestProperties {
    RBC("Red Blood Cell", 1, 0.0042, 0.0061),
    WBC("White Blood Cell", 2, 4.5, 12.0),
    HAEMOGLOBIN("Haemoglobin", 3, 113, 145),
    PLATELETS("Platelets", 4, 150, 475),
    GLUCOSE("Glucose", 5, 0.0, 7.8),
    HAEMATOCRIT("Haematocrit", 6, 0.33, 0.42),
    MEAN_CELL_VOLUME("Mean Cell Volume", 7, 74.0, 87.0),
    MEAN_CELL_HAEMATOCRIT("Mean Cell Haematocrit", 8, 24.0, 29.0);

    private final String name;
    private final int dbValue;
    private final double lowerBound;
    private final double upperBound;

    BloodTestProperties(String s, int i, double lb, double ub) {
        name = s;
        dbValue = i;
        lowerBound = lb;
        upperBound = ub;
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

    public double getLowerBound() {
        return this.lowerBound;
    }

    public double getUpperBound() {
        return this.upperBound;
    }
}
