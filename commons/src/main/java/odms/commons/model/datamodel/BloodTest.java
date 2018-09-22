package odms.commons.model.datamodel;

import odms.commons.model._enum.BloodTestProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BloodTest {

    private int bloodTestId;
    private double redBloodCellCount;
    private double whiteBloodCellCount;
    private double haemoglobinLevel;
    private double platelets;
    private double glucoseLevels;
    private double meanCellVolume;
    private double haematocrit;
    private double meanCellHaematocrit;
    private LocalDate testDate;
    private transient List<BloodTestProperties> lowValues;
    private transient List<BloodTestProperties> highValues;

    public BloodTest(double redBloodCellCount, double whiteBloodCellCount, double haemoglobinLevel, double platelets, double glucoseLevels, double meanCellVolume, double haematocrit, double meanCellHaematocrit, LocalDate testDate) {
        this.redBloodCellCount = redBloodCellCount;
        this.whiteBloodCellCount = whiteBloodCellCount;
        this.haemoglobinLevel = haemoglobinLevel;
        this.platelets = platelets;
        this.glucoseLevels = glucoseLevels;
        this.meanCellVolume = meanCellVolume;
        this.haematocrit = haematocrit;
        this.meanCellHaematocrit = meanCellHaematocrit;
        this.testDate = testDate;
        this.lowValues = new ArrayList<>();
        this.highValues = new ArrayList<>();
    }

    public BloodTest() {
        this.redBloodCellCount = 0.0;
        this.whiteBloodCellCount = 0.0;
        this.haemoglobinLevel = 0.0;
        this.platelets = 0.0;
        this.glucoseLevels = 0.0;
        this.meanCellVolume = 0.0;
        this.haematocrit = 0.0;
        this.meanCellHaematocrit = 0.0;
        this.testDate = null;
        this.lowValues = new ArrayList<>();
        this.highValues = new ArrayList<>();
    }

    public int getBloodTestId() {
        return bloodTestId;
    }

    public void setBloodTestId(int bloodTestId) {
        this.bloodTestId = bloodTestId;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public double getRedBloodCellCount() {
        return redBloodCellCount;
    }

    public void setRedBloodCellCount(double redBloodCellCount) {
        this.redBloodCellCount = redBloodCellCount;
    }

    public double getWhiteBloodCellCount() {
        return whiteBloodCellCount;
    }

    public void setWhiteBloodCellCount(double whiteBloodCellCount) {
        this.whiteBloodCellCount = whiteBloodCellCount;
    }

    public double getHaemoglobinLevel() {
        return haemoglobinLevel;
    }

    public void setHaemoglobinLevel(double haemoglobinLevel) {
        this.haemoglobinLevel = haemoglobinLevel;
    }

    public double getPlatelets() {
        return platelets;
    }

    public void setPlatelets(double platelets) {
        this.platelets = platelets;
    }

    public double getGlucoseLevels() {
        return glucoseLevels;
    }

    public void setGlucoseLevels(double glucoseLevels) {
        this.glucoseLevels = glucoseLevels;
    }

    public double getMeanCellVolume() {
        return meanCellVolume;
    }

    public void setMeanCellVolume(double meanCellVolume) {
        this.meanCellVolume = meanCellVolume;
    }

    public double getHaematocrit() {
        return haematocrit;
    }

    public void setHaematocrit(double haematocrit) {
        this.haematocrit = haematocrit;
    }

    public double getMeanCellHaematocrit() {
        return meanCellHaematocrit;
    }

    public void setMeanCellHaematocrit(double meanCellHaematocrit) {
        this.meanCellHaematocrit = meanCellHaematocrit;
    }

    public List<BloodTestProperties> getLowValues() {
        return lowValues;
    }

    public void setLowValues(List<BloodTestProperties> lowValues) {
        this.lowValues = lowValues;
    }

    public List<BloodTestProperties> getHighValues() {
        return highValues;
    }

    public void setHighValues(List<BloodTestProperties> highValues) {
        this.highValues = highValues;
    }

    @Override
    public String toString() {
        return "BloodTest{" +
                "redBloodCellCount='" + redBloodCellCount + '\'' +
                ", whiteBloodCellCount='" + whiteBloodCellCount + '\'' +
                ", haemoglobinLevel='" + haemoglobinLevel + '\'' +
                ", platelets='" + platelets + '\'' +
                ", glucoseLevels='" + glucoseLevels + '\'' +
                ", meanCellVolume='" + meanCellVolume + '\'' +
                ", haematocrit='" + haematocrit + '\'' +
                ", meanCellHaematocrit='" + meanCellHaematocrit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodTest bloodTest = (BloodTest) o;
        return bloodTestId == bloodTest.bloodTestId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bloodTestId);
    }
}
