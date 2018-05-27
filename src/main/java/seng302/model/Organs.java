package seng302.model;


import com.google.gson.annotations.SerializedName;

/**
 * Enum for organs that can be received/donated
 */
public enum Organs {
    @SerializedName("Liver")
    LIVER("Liver"),
    @SerializedName("Kidney")
    KIDNEY("Kidney"),
    @SerializedName("Pancreas")
    PANCREAS("Pancreas"),
    @SerializedName("Heart")
    HEART("Heart"),
    @SerializedName("Lung")
    LUNG("Lung"),
    @SerializedName("Intestine")
    INTESTINE("Intestine"),
    @SerializedName("Cornea")
    CORNEA("Cornea"),
    @SerializedName("Middle Ear")
    MIDDLE_EAR("Middle Ear"),
    @SerializedName("Skin")
    SKIN("Skin"),
    @SerializedName("Bone Marrow")
    BONE_MARROW("Bone Marrow"),
    @SerializedName("Bone")
    BONE("Bone"),
    @SerializedName("Connective Tissue")
    CONNECTIVE_TISSUE("Connective Tissue");

    public String organName;

    Organs(String organName) {
        this.organName = organName;
    }

    @Override
    public String toString() {
        return organName;
    }
}
