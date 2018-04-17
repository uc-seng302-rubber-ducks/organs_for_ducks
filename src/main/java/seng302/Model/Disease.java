package seng302.Model;

import java.time.LocalDate;

/**
 * Class to hold information
 * of donor's diseases
 *
 * @author acb116 - Aaron Bong
 */
public class Disease {
    private String name;
    private boolean isChronic;
    private boolean isCured;
    private LocalDate diagnosisDate;

    /**
     * Disease constructor that exposes
     * all attributes of Disease class
     * @param name of disease
     * @param isChronic true if disease is chronic, false otherwise
     * @param isCured true if disease is cured, false otherwise
     * @param diagnosisDate date of diagnosis done
     */
    public Disease(String name, boolean isChronic, boolean isCured, LocalDate diagnosisDate){
        this.name = name;
        this.isChronic = isChronic;
        this.isCured = isCured;
        this.diagnosisDate = diagnosisDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String diseaseName) {
        this.name = diseaseName;
    }

    public  Boolean getIsChronic() {
        return isChronic;
    }

    public void setIsChronic(boolean isChronic) {
        this.isChronic = isChronic;
    }

    public  Boolean getIsCured() {
        return isCured;
    }

    public void setIsCured(boolean isCured) {
        this.isCured = isCured;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }
}
