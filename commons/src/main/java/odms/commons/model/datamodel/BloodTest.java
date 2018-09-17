package odms.commons.model.datamodel;

import java.time.LocalDate;

public class BloodTest {
    double redBloodCellCount;
    double whiteBloodCellCount;
    double haemoglobinLevel;
    double platelets;
    double glucoseLevels;
    double meanCellVolume;
    double haematocrit;
    double meanCellHaematocrit;
    LocalDate requestedDate;
    LocalDate resultsRecived;

    public BloodTest() {
        this.redBloodCellCount = 0.0;
        this.whiteBloodCellCount = 0.0;
        this.haemoglobinLevel = 0.0;
        this.platelets = 0.0;
        this.glucoseLevels = 0.0;
        this.meanCellVolume = 0.0;
        this.haematocrit = 0.0;
        this.meanCellHaematocrit = 0.0;
        this.requestedDate = null;
        this.resultsRecived = null;
    }

    public BloodTest(double redBloodCellCount, double whiteBloodCellCount, double haemoglobinLevel, double platelets, double glucoseLevels, double meanCellVolume, double haematocrit, double meanCellHaematocrit, LocalDate requestedDate, LocalDate resultsRecived) {
        this.redBloodCellCount = redBloodCellCount;
        this.whiteBloodCellCount = whiteBloodCellCount;
        this.haemoglobinLevel = haemoglobinLevel;
        this.platelets = platelets;
        this.glucoseLevels = glucoseLevels;
        this.meanCellVolume = meanCellVolume;
        this.haematocrit = haematocrit;
        this.meanCellHaematocrit = meanCellHaematocrit;
        this.requestedDate = requestedDate;
        this.resultsRecived = resultsRecived;
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






}
