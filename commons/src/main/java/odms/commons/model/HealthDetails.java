package odms.commons.model;


import odms.commons.model.datamodel.BloodTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for health details for a user
 */
public class HealthDetails {

    private String birthGender;
    private String genderIdentity;
    private String alcoholConsumption;
    private boolean smoker;
    private Double height;
    private transient String heightText; //NOSONAR
    private Double weight;
    private transient String weightText; //NOSONAR
    private String bloodType;
    private List<BloodTest> bloodTests;


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
        this.bloodTests = new ArrayList<>();
    }

    public void addNewBloodTest(BloodTest bloodTest){
        bloodTests.add(bloodTest);
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getHeightText() {
        return heightText;
    }

    public void setHeightText(String height) {
        this.heightText = height;
    }

    public List<BloodTest> getBloodTests() {
        return bloodTests;
    }

    public void setBloodTests(List<BloodTest> bloodTests) {
        this.bloodTests = bloodTests;
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
                "\nbirthGender='" + birthGender + '\'' +
                ",\ngenderIdentity='" + genderIdentity + '\'' +
                ",\nalcoholConsumption='" + alcoholConsumption + '\'' +
                ",\nsmoker=" + smoker +
                ",\nheight=" + height +
                ",\nweight=" + weight +
                ",\nbloodType='" + bloodType + '\'' +
                '}';
    }
}
