package seng302.Model;


import com.google.gson.annotations.Expose;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Class for instantiating a medical procedure
 * @author Josh Burt
 */
public class MedicalProcedure {

    @Expose
    private LocalDate procedureDate;
    @Expose
    private String summary;
    @Expose
    private String description;
    @Expose
    private ArrayList<Organs> organsAffected;

    /**
     * Constructor for Medical Procedure
     * @param procedureDate
     * @param summary
     * @param description
     * @param organsAffected
     */
    public MedicalProcedure(LocalDate procedureDate, String summary, String description, ArrayList<Organs> organsAffected) {

        this.procedureDate = procedureDate;
        this.summary = summary;
        this.description = description;
        if (organsAffected == null){
            this.organsAffected = new ArrayList<>();
        } else {
            this.organsAffected = organsAffected;
        }
    }

    /**
     * Constructor for Medical Procedure
     * @param procedureDate
     * @param summary
     */
    public MedicalProcedure(LocalDate procedureDate, String summary) {
        this.procedureDate = procedureDate;
        this.summary = summary;
        organsAffected = new ArrayList<Organs>();
    }

    /**
     * Constructor for Medical Procedure
     */
    public MedicalProcedure() {
        organsAffected = new ArrayList<Organs>();
    }

    public LocalDate getProcedureDate() {
        return procedureDate;
    }

    public void setProcedureDate(LocalDate procedureDate) {
        this.procedureDate = procedureDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Organs> getOrgansAffected() {
        return organsAffected;
    }

    public void setOrgansAffected(ArrayList<Organs> organsAffected) {
        this.organsAffected = organsAffected;
    }

    public void addOrgan(Organs organ){
        organsAffected.add(organ);
    }

    public void removeOrgan(Organs organ){
        organsAffected.remove(organ);
    }

    @Override
    public String toString() {
        return "MedicalProcedure{" +
                "procedureDate=" + procedureDate +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", organsAffected=" + organsAffected +
                '}';
    }
}
