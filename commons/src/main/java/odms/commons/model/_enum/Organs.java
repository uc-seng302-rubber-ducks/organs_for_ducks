package odms.commons.model._enum;


import com.google.gson.annotations.SerializedName;

/**
 * Enum for organs that can be received/donated
 */
public enum Organs {

    @SerializedName("Liver")
    LIVER("Liver", 1, 24, 24),
    @SerializedName("Kidney")
    KIDNEY("Kidney", 2, 48, 72),
    @SerializedName("Pancreas")
    PANCREAS("Pancreas", 3, 12, 24),
    @SerializedName("Heart")
    HEART("Heart", 4, 4, 6),
    @SerializedName("Lung")
    LUNG("Lung", 5, 4, 6),
    @SerializedName("Intestine")
    INTESTINE("Intestine", 6, 8, 16), // https://www.organdonor.gov/about/process/matching.html#criteria
    @SerializedName("Cornea")
    CORNEA("Cornea", 7, 5 * 24, 7 * 24),
    @SerializedName("Middle Ear")
    MIDDLE_EAR("Middle Ear", 8, 24 * 365 * 3, 24 * 365 * 10),
    @SerializedName("Skin")
    SKIN("Skin", 9, 24 * 365 * 3, 24 * 365 * 10),
    @SerializedName("Bone Marrow")
    BONE_MARROW("Bone Marrow", 10, 0, 0),
    @SerializedName("Bone")
    BONE("Bone", 11, 24 * 365 * 3, 24 * 365 * 10),
    @SerializedName("Connective Tissue")
    CONNECTIVE_TISSUE("Connective Tissue", 12, 24 * 365 * 3, 24 * 365 * 10),
    @SerializedName("Test Organ")
    TEST_ORGAN("Test Organ", 13, 0.5,1);// An oran to test progress bars and tests

    private String organName;
    private int dbValue;
    private static final int HOURS_TO_SECONDS = 3600;
    private double lowerBoundSeconds;
    private long upperBoundSeconds;


    Organs(String organName, int dbValue, double lowerBoundSeconds, double storageSeconds) {
        this.organName = organName;
        this.dbValue = dbValue;
        this.lowerBoundSeconds = lowerBoundSeconds * HOURS_TO_SECONDS;
        this.upperBoundSeconds = (long) (storageSeconds * HOURS_TO_SECONDS);
    }


    /**
     * Takes a string argument for an organ and returns the organ if it exists
     * @param organName organ in the enum to make
     * @return organ as an object
     */
    public static Organs fromString(String organName){
        return Organs.valueOf(organName.replaceAll(" ", "_").toUpperCase());
    }

    @Override
    public String toString() {
        return organName;
    }

    public int getDbValue() {
        return dbValue;
    }

    public long getUpperBoundSeconds() {
        return upperBoundSeconds;
    }

    public double getLowerBoundSeconds() {
        return lowerBoundSeconds;
    }
}
