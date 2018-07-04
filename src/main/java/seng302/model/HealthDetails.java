package seng302.model;


/**
 * Class for health details for a user
 */
public class HealthDetails {

    private String birthGender;
    private String genderIdentity;
    private String alcoholConsumption;
    private boolean smoker;
    private double height;
    private transient String heightText;
    private double weight;
    private transient String weightText;
    private String bloodType;

    public HealthDetails() {
        this.birthGender = "";
        this.genderIdentity = "";
        this.alcoholConsumption = "None";
        this.smoker = false;
        this.height = 0.0;
        this.weight = 0.0;
        this.heightText = "";
        this.weightText = "";
        this.bloodType = "";
    }


    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getHeightText() {
        return heightText;
    }

    public void setHeightText(String height) {
        this.heightText = height;

    }

    public String getWeightText() {
        return weightText;
    }

    public void setWeightText(String weight) {
        this.weightText = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getBirthGender() {
        return birthGender;
    }

    public void setBirthGender(String birthGender) {
        if (genderIdentity == null) {
            genderIdentity = this.birthGender;
        }
        this.birthGender = birthGender;
    }

    public String getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(String genderIdentity) {
        this.genderIdentity = genderIdentity;
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public boolean isSmoker() {
        return smoker;
    }

    public void setSmoker(boolean smoker) {
        this.smoker = smoker;
    }

    @Override
    public String toString() {
        return "HealthDetails{" +
                "birthGender='" + birthGender + '\'' +
                ", genderIdentity='" + genderIdentity + '\'' +
                ", alcoholConsumption='" + alcoholConsumption + '\'' +
                ", smoker=" + smoker +
                ", height=" + height +
                ", heightText='" + heightText + '\'' +
                ", weight=" + weight +
                ", weightText='" + weightText + '\'' +
                ", bloodType='" + bloodType + '\'' +
                '}';
    }
}
