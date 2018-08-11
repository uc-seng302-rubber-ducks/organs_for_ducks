package odms.commons.model._enum;


import com.google.gson.annotations.SerializedName;

/**
 * Enum for organs that can be received/donated
 */
public enum Organs {

    @SerializedName("Liver")
    LIVER("Liver", 1, 0.01),
    @SerializedName("Kidney")
    KIDNEY("Kidney", 2, 72),
    @SerializedName("Pancreas")
    PANCREAS("Pancreas", 3, 24),
    @SerializedName("Heart")
    HEART("Heart", 4, 6),
    @SerializedName("Lung")
    LUNG("Lung", 5, 6),
    @SerializedName("Intestine")
    INTESTINE("Intestine", 6, 16), // https://www.organdonor.gov/about/process/matching.html#criteria
    @SerializedName("Cornea")
    CORNEA("Cornea", 7, 7 * 24),
    @SerializedName("Middle Ear")
    MIDDLE_EAR("Middle Ear", 8, 0),
    @SerializedName("Skin")
    SKIN("Skin", 9, 24 * 365 * 10),
    @SerializedName("Bone Marrow")
    BONE_MARROW("Bone Marrow", 10, 0),
    @SerializedName("Bone")
    BONE("Bone", 11, 24 * 365 * 10),
    @SerializedName("Connective Tissue")
    CONNECTIVE_TISSUE("Connective Tissue", 12, 24 * 365 * 10);

    private String organName;
    private int dbValue;
    private static final int HOURS_TO_SECONDS = 3600;
    private int storageSeconds;


    Organs(String organName, int dbValue, double storageSeconds) {
        this.organName = organName;
        this.dbValue = dbValue;
        this.storageSeconds = (int) (storageSeconds * HOURS_TO_SECONDS);
    }

    @Override
    public String toString() {
        return organName;
    }

    public int getDbValue() {
        return dbValue;
    }

    public int getStorageSeconds() {
        return storageSeconds;
    }
}
