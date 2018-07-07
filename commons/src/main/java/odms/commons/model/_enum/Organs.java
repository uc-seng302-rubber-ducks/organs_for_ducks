package odms.commons.model._enum;


import com.google.gson.annotations.SerializedName;

/**
 * Enum for organs that can be received/donated
 */
public enum Organs {
    @SerializedName("Liver")
    LIVER("Liver", 1),
    @SerializedName("Kidney")
    KIDNEY("Kidney", 2),
    @SerializedName("Pancreas")
    PANCREAS("Pancreas", 3),
    @SerializedName("Heart")
    HEART("Heart", 4),
    @SerializedName("Lung")
    LUNG("Lung", 5),
    @SerializedName("Intestine")
    INTESTINE("Intestine", 6),
    @SerializedName("Cornea")
    CORNEA("Cornea", 7),
    @SerializedName("Middle Ear")
    MIDDLE_EAR("Middle Ear", 8),
    @SerializedName("Skin")
    SKIN("Skin", 9),
    @SerializedName("Bone Marrow")
    BONE_MARROW("Bone Marrow", 10),
    @SerializedName("Bone")
    BONE("Bone", 11),
    @SerializedName("Connective Tissue")
    CONNECTIVE_TISSUE("Connective Tissue", 12);

    private String organName;
    private int dbValue;

    Organs(String organName, int dbValue) {
        this.organName = organName;
        this.dbValue = dbValue;
    }

    @Override
    public String toString() {
        return organName;
    }

    public int getDbValue() {
        return dbValue;
    }
}
