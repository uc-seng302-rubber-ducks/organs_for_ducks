package seng302.Model;

import com.google.gson.annotations.Expose;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Class to hold information
 * of donor's diseases
 *
 * @author acb116 - Aaron Bong
 */
public class Disease {
    @Expose
    private String name;
    @Expose
    private boolean isChronic;
    @Expose
    private boolean isCured;
    @Expose
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

    public transient Comparator<Disease> diseaseDateComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease o1, Disease o2) {
            LocalDate diseaseDate1 = o1.getDiagnosisDate();
            LocalDate diseaseDate2 = o2.getDiagnosisDate();

            return diseaseDate1.compareTo(diseaseDate2);
        }
    };

    public transient Comparator<Disease> diseaseChronicComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease o1, Disease o2) {
            boolean diseaseChronic1 = o1.getIsChronic();
            boolean diseaseChronic2 = o2.getIsChronic();

            return (diseaseChronic1 != diseaseChronic2) ? (diseaseChronic1) ? -1 : 1 : 0;
            /*
            if (b1 != b2){
                if (b1 == true){
                    return -1;
                }
                if (b1 == false){
                    return 1;
                }
            }
            return 0;
             */
        }
    };

    public transient Comparator<Disease> diseaseNameComparator = new Comparator<Disease>() {
        @Override
        public int compare(Disease o1, Disease o2) {
            String diseaseName1 = o1.getName();
            String diseaseName2 = o2.getName();

            return diseaseName1.compareTo(diseaseName2);
        }
    };

    @Override
    public String toString(){
        return name;
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
